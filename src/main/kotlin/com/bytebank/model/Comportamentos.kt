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

/** Contas que reiniciam algum contador periódico a cada fechamento mensal (ex.: cota de saques gratuitos). */
interface LimiteMensalReiniciavel {
    fun reiniciarLimiteMensal()
}
