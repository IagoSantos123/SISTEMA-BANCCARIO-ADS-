package com.bytebank

import com.bytebank.exception.OperacaoBancariaException
import com.bytebank.model.Cliente
import com.bytebank.model.ContaBancaria
import com.bytebank.model.ContaCorrente
import com.bytebank.model.ContaInvestimento
import com.bytebank.model.ContaPoupanca
import com.bytebank.service.Banco
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
    val banco = Banco("ByteBank Evolution")
    banco.exibirCabecalho("BANCO ${banco.nome.uppercase()}")

    // ---------------------------------------------------------------
    // 1. Criação de clientes e de diferentes tipos de conta (polimorfismo)
    // ---------------------------------------------------------------
    val maria = banco.cadastrarCliente(Cliente("Maria Silva", "111.222.333-44"))
    val joao = banco.cadastrarCliente(Cliente("João Souza", "222.333.444-55"))
    val ana = banco.cadastrarCliente(Cliente("Ana Costa", "333.444.555-66"))

    val contaCorrente: ContaBancaria = ContaCorrente(maria, saldoInicial = 2500.0, limiteChequeEspecial = 500.0)
    val contaPoupanca: ContaBancaria = ContaPoupanca(joao, saldoInicial = 1000.0)
    val contaInvestimento: ContaBancaria = ContaInvestimento(ana, saldoInicial = 5000.0, carenciaSaques = 1)

    listOf(contaCorrente, contaPoupanca, contaInvestimento).forEach { banco.registrarConta(it) }

    println("\nContas criadas:")
    contaCorrente.exibirDados()
    println()
    contaPoupanca.exibirDados()
    println()
    contaInvestimento.exibirDados()

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

    // ---------------------------------------------------------------
    // 4. Fechamento mensal: taxas (conta corrente) e rendimentos (poupança/investimento)
    // ---------------------------------------------------------------
    banco.exibirCabecalho("FECHAMENTO MENSAL")
    banco.processarFechamentoMensal()
    println("Novo saldo conta corrente (${contaCorrente.numero}): ${Formatador.moeda(contaCorrente.consultarSaldo())}")
    println("Novo saldo poupança (${contaPoupanca.numero}): ${Formatador.moeda(contaPoupanca.consultarSaldo())}")
    println("Novo saldo investimento (${contaInvestimento.numero}): ${Formatador.moeda(contaInvestimento.consultarSaldo())}")

    // Segundo mês de rendimento, apenas para liberar a carência da conta investimento
    banco.processarFechamentoMensal()
    operacao("Saque de ${Formatador.moeda(100.0)} na conta ${contaInvestimento.numero} (após carência)") {
        contaInvestimento.sacar(100.0)
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
