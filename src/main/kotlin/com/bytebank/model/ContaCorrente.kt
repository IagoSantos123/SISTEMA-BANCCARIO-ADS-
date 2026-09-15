package com.bytebank.model

import com.bytebank.exception.SaldoInsuficienteException
import com.bytebank.util.Formatador

/**
 * Conta Corrente: possui limite de cheque especial e cobra taxa mensal de manutenção.
 * É o exemplo clássico de conta "com custo, mas com margem extra para saque".
 */
class ContaCorrente(
    cliente: Cliente,
    saldoInicial: Double = 0.0,
    val limiteChequeEspecial: Double = 500.0
) : ContaBancaria(gerarNumeroConta(), cliente, saldoInicial), TaxavelMensalmente {

    init {
        cliente.vincularConta(this)
    }

    override val tipoConta: String = "Conta Corrente"
    override val percentualTaxa: Double = 0.012 // 1,2% ao mês sobre o saldo

    /** Sobrescrito: a Conta Corrente permite saldo negativo até o limite do cheque especial. */
    override fun sacar(valor: Double) {
        validarValorPositivo(valor)
        val saldoDisponivel = saldo + limiteChequeEspecial
        if (valor > saldoDisponivel) {
            throw SaldoInsuficienteException(saldo, valor)
        }
        saldo -= valor
        registrarTransacao(Transacao(TipoTransacao.SAQUE, valor))
    }

    override fun aplicarTaxaMensal() {
        val taxa = saldo * percentualTaxa
        debitarTaxaOuRendimento(-taxa, TipoTransacao.TAXA_MANUTENCAO)
        println("Taxa de manutenção aplicada na conta $numero: ${Formatador.moeda(taxa)}")
    }

    override fun exibirDados() {
        super.exibirDados()
        println("Limite de cheque especial: ${Formatador.moeda(limiteChequeEspecial)}")
    }
}
