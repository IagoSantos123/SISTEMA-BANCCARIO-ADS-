// Rascunho enviado por Flavia Tavares Nascimento (commit "Add files via upload").
// Mantido aqui apenas como referência histórica: é uma implementação paralela do
// mesmo exercício, não utilizada pela solução oficial do projeto (ver com.bytebank.model/service).
package com.bytebank.rascunhos.bancov1

import java.util.Locale

class Cliente(
    val nome: String,
    val cpf: String
) {
    fun exibirDadosCliente() {
        println("Nome: $nome")
        println("CPF: $cpf")
    }
}

data class Operacao(
    val tipo: String,
    val valor: Double,
    val descricao: String
)

abstract class ContaBancaria(
    val numeroConta: Int,
    val cliente: Cliente,
    saldoInicial: Double
) {
    protected var saldo: Double = saldoInicial
    protected val historico = mutableListOf<Operacao>()

    fun depositar(valor: Double) {
        if (valor > 0) {
            saldo += valor
            historico.add(Operacao("Depósito", valor, "Depósito realizado com sucesso"))
            println("Depósito realizado com sucesso!")
        } else {
            println("Valor de depósito inválido!")
        }
    }

    fun sacar(valor: Double) {
        if (valor <= 0) {
            println("Valor de saque inválido!")
        } else if (valor <= saldo) {
            saldo -= valor
            historico.add(Operacao("Saque", valor, "Saque realizado com sucesso"))
            println("Saque realizado com sucesso!")
        } else {
            println("Saldo insuficiente!")
        }
    }

    fun consultarSaldo() {
        println("Saldo atual: R$ $saldo")
    }

    fun obterSaldo(): Double = saldo

    fun exibirDados() {
        println("==============================")
        println("Cliente: ${cliente.nome}")
        println("CPF: ${cliente.cpf}")
        println("Conta: $numeroConta")
        println("Tipo da conta: ${tipoConta()}")
        println("Saldo: R$ $saldo")
        println("==============================")
    }

    fun exibirHistorico() {
        println("==============================")
        println("HISTÓRICO DE OPERAÇÕES")
        println("==============================")
        if (historico.isEmpty()) {
            println("Nenhuma operação realizada.")
        } else {
            for (operacao in historico) {
                println("Tipo: ${operacao.tipo}")
                println("Valor: R$ ${operacao.valor}")
                println("Descrição: ${operacao.descricao}")
                println("------------------------------")
            }
        }
    }

    abstract fun tipoConta(): String
    abstract fun aplicarRegraEspecifica()
}

class Banco(val nome: String = "ByteBank Evolution") {

    private val contas = mutableMapOf<Int, ContaBancaria>()

    fun cadastrarConta(conta: ContaBancaria): Boolean {
        if (contas.containsKey(conta.numeroConta)) {
            println("Erro: já existe uma conta com o número ${conta.numeroConta}.")
            return false
        }
        contas[conta.numeroConta] = conta
        println("Conta ${conta.numeroConta} (${conta.cliente.nome}) cadastrada com sucesso.")
        return true
    }

    fun buscarContaPorNumero(numeroConta: Int): ContaBancaria? {
        return contas[numeroConta]
    }

    fun buscarContasPorCliente(nome: String): List<ContaBancaria> {
        return contas.values.filter { it.cliente.nome.equals(nome, ignoreCase = true) }
    }

    fun listarContas(): List<ContaBancaria> {
        return contas.values.toList()
    }

    fun transferir(numeroOrigem: Int, numeroDestino: Int, valor: Double): Boolean {
        if (valor <= 0) {
            println("Valor de transferência inválido!")
            return false
        }

        if (numeroOrigem == numeroDestino) {
            println("Não é possível transferir para a mesma conta!")
            return false
        }

        val origem = buscarContaPorNumero(numeroOrigem)
        val destino = buscarContaPorNumero(numeroDestino)

        if (origem == null || destino == null) {
            println("Erro: conta de origem ou destino não encontrada.")
            return false
        }

        if (valor > origem.obterSaldo()) {
            println("Transferência não realizada: saldo insuficiente na conta $numeroOrigem!")
            return false
        }

        origem.sacar(valor)
        destino.depositar(valor)

        println(
            "Transferência de R$%.2f realizada: conta $numeroOrigem -> conta $numeroDestino."
                .format(Locale.US, valor)
        )
        return true
    }

    fun exibirTodasAsContas() {
        println("==================================================")
        println("BANCO $nome — CONTAS CADASTRADAS")
        println("==================================================")
        if (contas.isEmpty()) {
            println("Nenhuma conta cadastrada.")
            return
        }
        contas.values.forEach { conta ->
            conta.exibirDados()
        }
    }
}

class ContaCorrenteExemplo(numeroConta: Int, cliente: Cliente, saldoInicial: Double) :
    ContaBancaria(numeroConta, cliente, saldoInicial) {
    override fun tipoConta() = "Conta Corrente"
    override fun aplicarRegraEspecifica() {
        println("Aplicando taxa mensal de manutenção...")
        sacar(30.0)
    }
}

fun main() {
    val banco = Banco()

    val maria = Cliente("Maria Silva", "111.111.111-11")
    val joao = Cliente("João Souza", "222.222.222-22")

    val contaMaria = ContaCorrenteExemplo(1001, maria, 2500.0)
    val contaJoao = ContaCorrenteExemplo(2002, joao, 1000.0)

    banco.cadastrarConta(contaMaria)
    banco.cadastrarConta(contaJoao)

    println()
    banco.transferir(1001, 2002, 300.0)
    banco.transferir(1001, 2002, 99999.0)
    banco.transferir(1001, 9999, 50.0)

    println()
    banco.exibirTodasAsContas()
    contaMaria.exibirHistorico()
    contaJoao.exibirHistorico()
}
