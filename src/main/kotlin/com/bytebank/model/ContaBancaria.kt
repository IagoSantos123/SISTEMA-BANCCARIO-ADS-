package com.bytebank.model

import com.bytebank.exception.SaldoInsuficienteException
import com.bytebank.exception.ValorInvalidoException
import com.bytebank.util.Formatador

/**
 * Classe base abstrata de todas as contas do ByteBank Evolution.
 *
 * Concentra tudo que é comum a qualquer conta (depósito, saque, transferência,
 * consulta de saldo e histórico), garantindo que a regra "saldo nunca fica
 * negativo" seja aplicada em um único lugar. Comportamentos que variam por
 * tipo de conta (taxa, rendimento, exibição) ficam abertos para especialização
 * nas subclasses.
 */
abstract class ContaBancaria protected constructor(
    val numero: Int,
    val titular: Cliente,
    saldoInicial: Double = 0.0
) {
    companion object {
        private var proximoNumero = 1000

        /** Gera um número de conta sequencial, evitando duplicidade e input manual. */
        internal fun gerarNumeroConta(): Int = ++proximoNumero
    }

    var saldo: Double = saldoInicial
        protected set

    var ativa: Boolean = true
        private set

    private val _historico = mutableListOf<Transacao>()
    val historico: List<Transacao>
        get() = _historico.toList()

    /** Nome exibido do tipo de conta (ex.: "Conta Corrente"). Cada subclasse define o seu. */
    abstract val tipoConta: String

    fun depositar(valor: Double) {
        validarValorPositivo(valor)
        saldo += valor
        registrarTransacao(Transacao(TipoTransacao.DEPOSITO, valor))
    }

    /**
     * Saque padrão. Aberto para override porque algumas contas (ex.: investimento
     * com carência) podem restringir ou taxar o saque de forma diferente.
     */
    open fun sacar(valor: Double) {
        validarValorPositivo(valor)
        if (valor > saldo) {
            throw SaldoInsuficienteException(saldo, valor)
        }
        saldo -= valor
        registrarTransacao(Transacao(TipoTransacao.SAQUE, valor))
    }

    fun transferir(valor: Double, contaDestino: ContaBancaria) {
        require(contaDestino.numero != this.numero) { "Não é possível transferir para a própria conta." }
        this.sacar(valor)
        // Removemos o registro de SAQUE genérico e registramos como transferência,
        // mantendo o histórico semanticamente correto para quem lê o extrato.
        this.substituirUltimaTransacaoPor(
            Transacao(TipoTransacao.TRANSFERENCIA_ENVIADA, valor, descricaoAdicional = "para conta ${contaDestino.numero}")
        )
        contaDestino.creditarTransferencia(valor, this)
    }

    /** Acesso protegido para a subclasse escolher a estratégia de cobrança de taxa. */
    protected fun debitarTaxaOuRendimento(valor: Double, tipo: TipoTransacao) {
        saldo += valor // valor negativo para taxa, positivo para rendimento
        registrarTransacao(Transacao(tipo, kotlin.math.abs(valor)))
    }

    fun desativarConta() {
        ativa = false
    }

    fun consultarSaldo(): Double = saldo

    /** Exibição padrão dos dados da conta; subclasses podem enriquecer via override. */
    open fun exibirDados() {
        println("Cliente: ${titular.nome}")
        println("Conta: $numero ($tipoConta)")
        println("Saldo Atual: ${Formatador.moeda(saldo)}")
    }

    fun exibirHistorico() {
        if (_historico.isEmpty()) {
            println("Nenhuma operação registrada para a conta $numero.")
            return
        }
        _historico.forEach { println(it.resumo()) }
    }

    private fun creditarTransferencia(valor: Double, contaOrigem: ContaBancaria) {
        saldo += valor
        registrarTransacao(
            Transacao(TipoTransacao.TRANSFERENCIA_RECEBIDA, valor, descricaoAdicional = "da conta ${contaOrigem.numero}")
        )
    }

    private fun substituirUltimaTransacaoPor(transacao: Transacao) {
        if (_historico.isNotEmpty()) _historico.removeAt(_historico.lastIndex)
        registrarTransacao(transacao)
    }

    protected fun registrarTransacao(transacao: Transacao) {
        _historico.add(transacao)
    }

    protected fun validarValorPositivo(valor: Double) {
        if (valor <= 0.0) throw ValorInvalidoException(valor)
    }
}
