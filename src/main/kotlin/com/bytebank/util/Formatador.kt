package com.bytebank.util

import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Utilitário central de formatação, evitando repetição de Locale/NumberFormat pelo sistema. */
object Formatador {

    private val localePtBr = Locale.Builder().setLanguage("pt").setRegion("BR").build()
    private val formatoMoeda = NumberFormat.getCurrencyInstance(localePtBr)
    val formatoDataHora: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    fun moeda(valor: Double): String = formatoMoeda.format(valor)
}
