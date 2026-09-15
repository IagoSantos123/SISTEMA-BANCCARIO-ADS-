package com.bytebank

import com.bytebank.model.Cliente
import com.bytebank.model.ContaBancaria
import com.bytebank.model.ContaCorrente
import com.bytebank.model.ContaInvestimento
import com.bytebank.model.ContaPoupanca
import com.bytebank.service.Banco

/** Cenário de demonstração compartilhado entre a versão de console e a interface gráfica. */
object DemoData {
    fun criarBancoDemonstracao(): Banco {
        val banco = Banco("ByteBank Evolution")

        val maria = banco.cadastrarCliente(Cliente("Maria Silva", "111.222.333-44"))
        val joao = banco.cadastrarCliente(Cliente("João Souza", "222.333.444-55"))
        val ana = banco.cadastrarCliente(Cliente("Ana Costa", "333.444.555-66"))

        val contaCorrente: ContaBancaria = ContaCorrente(maria, saldoInicial = 2500.0, limiteChequeEspecial = 500.0)
        val contaPoupanca: ContaBancaria = ContaPoupanca(joao, saldoInicial = 1000.0)
        val contaInvestimento: ContaBancaria = ContaInvestimento(ana, saldoInicial = 5000.0, carenciaSaques = 1)

        listOf(contaCorrente, contaPoupanca, contaInvestimento).forEach { banco.registrarConta(it) }
        return banco
    }
}
