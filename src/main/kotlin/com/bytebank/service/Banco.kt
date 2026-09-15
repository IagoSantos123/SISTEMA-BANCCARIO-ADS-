package com.bytebank.service

import com.bytebank.model.Cliente
import com.bytebank.model.ContaBancaria
import com.bytebank.model.LimiteMensalReiniciavel
import com.bytebank.model.RendimentoMensal
import com.bytebank.model.TaxavelMensalmente

/**
 * Fachada do sistema bancário. Mantém a lista de clientes/contas cadastradas
 * e sabe processar o fechamento mensal (taxas e rendimentos) de forma
 * polimórfica, sem precisar saber o tipo concreto de cada conta.
 */
class Banco(val nome: String) {

    private val _clientes = mutableListOf<Cliente>()
    val clientes: List<Cliente>
        get() = _clientes.toList()

    private val _contas = mutableListOf<ContaBancaria>()
    val contas: List<ContaBancaria>
        get() = _contas.toList()

    fun cadastrarCliente(cliente: Cliente): Cliente {
        _clientes.add(cliente)
        return cliente
    }

    fun registrarConta(conta: ContaBancaria) {
        _contas.add(conta)
    }

    /** Aplica a rotina de fechamento mensal em todas as contas, cada uma com sua regra própria. */
    fun processarFechamentoMensal() {
        println("Processando fechamento mensal para ${_contas.size} conta(s)...")
        for (conta in _contas) {
            when (conta) {
                is TaxavelMensalmente -> conta.aplicarTaxaMensal()
                is RendimentoMensal -> conta.aplicarRendimento()
                is LimiteMensalReiniciavel -> conta.reiniciarLimiteMensal()
                else -> println("Conta ${conta.numero} não possui regra de fechamento mensal.")
            }
        }
    }

    fun exibirExtratoGeral() {
        exibirCabecalho("EXTRATO GERAL - $nome")
        for (conta in _contas) {
            conta.exibirDados()
            println("-".repeat(36))
        }
    }

    fun exibirCabecalho(titulo: String) {
        val linha = "=".repeat(36)
        println()
        println(linha)
        println(titulo)
        println(linha)
    }
}
