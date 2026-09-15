package com.bytebank.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bytebank.model.ContaBancaria
import com.bytebank.model.ContaCorrente
import com.bytebank.service.Banco
import com.bytebank.util.Formatador

/** Retrato imutável de uma conta, pronto para ser exibido pela UI (a UI nunca toca a entidade de domínio direto). */
data class ContaResumo(
    val numero: Int,
    val titular: String,
    val tipoConta: String,
    val saldo: Double,
    val detalhe: String?,
    val historico: List<String>
)

sealed class Mensagem(val texto: String) {
    class Sucesso(texto: String) : Mensagem(texto)
    class Erro(texto: String) : Mensagem(texto)
}

/**
 * Ponte entre a UI (Compose) e o domínio bancário já existente (Banco/ContaBancaria).
 * Nenhuma regra de negócio mora aqui: cada ação apenas chama o método correspondente
 * do domínio e traduz sucesso/exceção em uma mensagem para a tela.
 */
class BancoController(private val banco: Banco) {

    val nomeBanco: String get() = banco.nome

    var contas by mutableStateOf(carregarContas())
        private set

    var numeroSelecionado by mutableStateOf(contas.firstOrNull()?.numero)
        private set

    var mensagem by mutableStateOf<Mensagem?>(null)
        private set

    val contaSelecionada: ContaResumo?
        get() = contas.find { it.numero == numeroSelecionado }

    fun selecionar(numero: Int) {
        numeroSelecionado = numero
    }

    fun limparMensagem() {
        mensagem = null
    }

    fun depositar(valor: Double) = executar { conta ->
        conta.depositar(valor)
        "Depósito de ${Formatador.moeda(valor)} realizado na conta ${conta.numero}."
    }

    fun sacar(valor: Double) = executar { conta ->
        conta.sacar(valor)
        "Saque de ${Formatador.moeda(valor)} realizado na conta ${conta.numero}."
    }

    fun transferir(valor: Double, numeroDestino: Int) = executar { conta ->
        val destino = banco.contas.find { it.numero == numeroDestino }
            ?: error("Conta de destino não encontrada.")
        conta.transferir(valor, destino)
        "Transferência de ${Formatador.moeda(valor)} enviada para a conta $numeroDestino."
    }

    fun processarFechamentoMensal() {
        banco.processarFechamentoMensal()
        contas = carregarContas()
        mensagem = Mensagem.Sucesso("Fechamento mensal processado para ${contas.size} conta(s).")
    }

    private fun executar(acao: (ContaBancaria) -> String) {
        val conta = banco.contas.find { it.numero == numeroSelecionado }
        if (conta == null) {
            mensagem = Mensagem.Erro("Selecione uma conta antes de operar.")
            return
        }
        mensagem = try {
            Mensagem.Sucesso(acao(conta))
        } catch (e: Exception) {
            Mensagem.Erro(e.message ?: "Não foi possível concluir a operação.")
        }
        contas = carregarContas()
    }

    private fun carregarContas(): List<ContaResumo> = banco.contas.map { it.paraResumo() }

    private fun ContaBancaria.paraResumo(): ContaResumo {
        val detalhe = (this as? ContaCorrente)?.let {
            "Limite de cheque especial: ${Formatador.moeda(it.limiteChequeEspecial)}"
        }
        return ContaResumo(
            numero = numero,
            titular = titular.nome,
            tipoConta = tipoConta,
            saldo = saldo,
            detalhe = detalhe,
            historico = historico.asReversed().map { it.resumo() }
        )
    }
}

/** Converte texto digitado pelo usuário em valor monetário, aceitando vírgula. Texto inválido vira -1, deixando a própria regra de negócio (ValorInvalidoException) recusar a operação. */
fun parseValorOuInvalido(texto: String): Double =
    texto.trim().replace(",", ".").toDoubleOrNull() ?: -1.0
