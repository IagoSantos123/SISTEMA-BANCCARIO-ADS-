package com.bytebank

import com.bytebank.exception.OperacaoBancariaException
import com.bytebank.util.Formatador

/**
 * Executa uma operação bancária protegida contra as regras de negócio do banco,
 * evitando duplicar o mesmo bloco try/catch em cada chamada dentro do main().
 */
fun operacao(descricao: String, acao: () -> Unit) {
    try {
        acao()
        println("$descricao: sucesso!")
    } catch (e: OperacaoBancariaException) {
        println("$descricao: FALHOU -> ${e.message}")
    }
}

fun main() {
    val banco = DemoData.criarBancoDemonstracao()
    banco.exibirCabecalho("BANCO ${banco.nome.uppercase()}")

    val (contaCorrente, contaPoupanca, contaInvestimento, contaSalario) = banco.contas

    println("\nContas criadas:")
    contaCorrente.exibirDados()
    println()
    contaPoupanca.exibirDados()
    println()
    contaInvestimento.exibirDados()
    println()
    contaSalario.exibirDados()

    // ---------------------------------------------------------------
    // 2. Depósitos (válidos e inválidos)
    // ---------------------------------------------------------------
    banco.exibirCabecalho("DEPOSITOS")
    operacao("Depósito de ${Formatador.moeda(500.0)} na conta ${contaCorrente.numero}") {
        contaCorrente.depositar(500.0)
    }
    operacao("Depósito inválido de ${Formatador.moeda(-50.0)} na conta ${contaCorrente.numero}") {
        contaCorrente.depositar(-50.0)
    }

    // ---------------------------------------------------------------
    // 3. Saques (válido, e saldo insuficiente)
    // ---------------------------------------------------------------
    banco.exibirCabecalho("SAQUES")
    operacao("Saque de ${Formatador.moeda(200.0)} na conta ${contaCorrente.numero}") {
        contaCorrente.sacar(200.0)
    }
    operacao("Saque de ${Formatador.moeda(10_000.0)} na conta ${contaCorrente.numero}") {
        contaCorrente.sacar(10_000.0)
    }
    operacao("Saque de ${Formatador.moeda(100.0)} na conta ${contaInvestimento.numero} (em carência)") {
        contaInvestimento.sacar(100.0)
    }
    operacao("Saque de ${Formatador.moeda(300.0)} na conta ${contaSalario.numero} (1º saque gratuito do mês)") {
        contaSalario.sacar(300.0)
    }
    operacao("Saque de ${Formatador.moeda(50.0)} na conta ${contaSalario.numero} (2º saque no mesmo mês)") {
        contaSalario.sacar(50.0)
    }

    // ---------------------------------------------------------------
    // 4. Fechamento mensal: taxas (conta corrente) e rendimentos (poupança/investimento)
    // ---------------------------------------------------------------
    banco.exibirCabecalho("FECHAMENTO MENSAL")
    banco.processarFechamentoMensal()
    println("Novo saldo conta corrente (${contaCorrente.numero}): ${Formatador.moeda(contaCorrente.consultarSaldo())}")
    println("Novo saldo poupança (${contaPoupanca.numero}): ${Formatador.moeda(contaPoupanca.consultarSaldo())}")
    println("Novo saldo investimento (${contaInvestimento.numero}): ${Formatador.moeda(contaInvestimento.consultarSaldo())}")

    // Segundo mês: libera a carência da conta investimento e reinicia a cota de saques da conta salário
    banco.processarFechamentoMensal()
    operacao("Saque de ${Formatador.moeda(100.0)} na conta ${contaInvestimento.numero} (após carência)") {
        contaInvestimento.sacar(100.0)
    }
    operacao("Saque de ${Formatador.moeda(50.0)} na conta ${contaSalario.numero} (novo mês, cota reiniciada)") {
        contaSalario.sacar(50.0)
    }

    // ---------------------------------------------------------------
    // 5. Transferência entre contas
    // ---------------------------------------------------------------
    banco.exibirCabecalho("TRANSFERENCIAS")
    operacao("Transferência de ${Formatador.moeda(100.0)} da conta ${contaCorrente.numero} para a conta ${contaPoupanca.numero}") {
        contaCorrente.transferir(100.0, contaPoupanca)
    }

    // ---------------------------------------------------------------
    // 6. Extrato geral de todas as contas do banco
    // ---------------------------------------------------------------
    banco.exibirExtratoGeral()

    // ---------------------------------------------------------------
    // 7. Histórico de operações por conta
    // ---------------------------------------------------------------
    banco.exibirCabecalho("HISTORICO DE OPERACOES")
    for (conta in banco.contas) {
        println("\nConta ${conta.numero} - ${conta.titular.nome}:")
        conta.exibirHistorico()
    }
}
