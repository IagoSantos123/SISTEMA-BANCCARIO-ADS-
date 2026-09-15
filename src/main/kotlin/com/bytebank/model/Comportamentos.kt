package com.bytebank.model

/** Contas que cobram taxa periódica de manutenção (ex.: Conta Corrente). */
interface TaxavelMensalmente {
    val percentualTaxa: Double
    fun aplicarTaxaMensal()
}

/** Contas que geram rendimento periódico sobre o saldo (ex.: Poupança, Investimento). */
interface RendimentoMensal {
    val percentualRendimento: Double
    fun aplicarRendimento()
}
