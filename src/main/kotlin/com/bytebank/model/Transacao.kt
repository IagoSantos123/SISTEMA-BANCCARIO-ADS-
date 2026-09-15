package com.bytebank.model

import com.bytebank.util.Formatador
import java.time.LocalDateTime

enum class TipoTransacao(val descricao: String) {
    DEPOSITO("Depósito"),
    SAQUE("Saque"),
    TRANSFERENCIA_ENVIADA("Transferência enviada"),
    TRANSFERENCIA_RECEBIDA("Transferência recebida"),
    TAXA_MANUTENCAO("Taxa de manutenção"),
    RENDIMENTO("Rendimento")
}

/**
 * Registro imutável de uma movimentação. Cada conta mantém sua própria lista
 * de transações, formando o "histórico simples de operações" exigido pelo banco.
 */
data class Transacao(
    val tipo: TipoTransacao,
    val valor: Double,
    val dataHora: LocalDateTime = LocalDateTime.now(),
    val descricaoAdicional: String? = null
) {
    fun resumo(): String {
        val base = "${tipo.descricao} de ${Formatador.moeda(valor)}"
        val complemento = descricaoAdicional?.let { " ($it)" } ?: ""
        return "[${dataHora.format(Formatador.formatoDataHora)}] $base$complemento realizado(a)."
    }
}
