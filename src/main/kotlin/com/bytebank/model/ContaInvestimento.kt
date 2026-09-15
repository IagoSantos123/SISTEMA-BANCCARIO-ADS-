package com.bytebank.model

import com.bytebank.exception.OperacaoNaoSuportadaException
import com.bytebank.util.Formatador

/**
 * Conta Investimento: rende mais que a poupança, mas exige carência mínima
 * antes de permitir saque — prova de que o sistema suporta novos tipos de
 * conta com regras totalmente próprias sem alterar o código já existente
 * (princípio Aberto/Fechado).
 */
class ContaInvestimento(
    cliente: Cliente,
    saldoInicial: Double = 0.0,
    private val carenciaSaques: Int = 1
) : ContaBancaria(gerarNumeroConta(), cliente, saldoInicial), RendimentoMensal {

    init {
        cliente.vincularConta(this)
    }

    override val tipoConta: String = "Conta Investimento"
    override val percentualRendimento: Double = 0.015 // 1,5% ao mês

    private var mesesDecorridos: Int = 0

    override fun sacar(valor: Double) {
        if (mesesDecorridos < carenciaSaques) {
            throw OperacaoNaoSuportadaException(
                "Conta Investimento $numero está em período de carência. " +
                    "Saques liberados após $carenciaSaques mês(es) de rendimento aplicado."
            )
        }
        super.sacar(valor)
    }

    override fun aplicarRendimento() {
        val rendimento = saldo * percentualRendimento
        debitarTaxaOuRendimento(rendimento, TipoTransacao.RENDIMENTO)
        mesesDecorridos++
        println("Rendimento aplicado na conta $numero: ${Formatador.moeda(rendimento)}")
    }
}
