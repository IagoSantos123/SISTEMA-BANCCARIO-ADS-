package com.bytebank.model

import com.bytebank.util.Formatador

/**
 * Conta Poupança: não cobra taxa de manutenção, mas rende um percentual
 * mensal sobre o saldo. Usa o saque padrão herdado (não permite saldo negativo).
 */
class ContaPoupanca(
    cliente: Cliente,
    saldoInicial: Double = 0.0
) : ContaBancaria(gerarNumeroConta(), cliente, saldoInicial), RendimentoMensal {

    init {
        cliente.vincularConta(this)
    }

    override val tipoConta: String = "Conta Poupança"
    override val percentualRendimento: Double = 0.006 // 0,6% ao mês

    override fun aplicarRendimento() {
        val rendimento = saldo * percentualRendimento
        debitarTaxaOuRendimento(rendimento, TipoTransacao.RENDIMENTO)
        println("Rendimento aplicado na conta $numero: ${Formatador.moeda(rendimento)}")
    }
}
