package com.bytebank

import com.bytebank.exception.OperacaoNaoSuportadaException
import com.bytebank.exception.SaldoInsuficienteException
import com.bytebank.exception.ValorInvalidoException
import com.bytebank.model.Cliente
import com.bytebank.model.ContaCorrente
import com.bytebank.model.ContaInvestimento
import com.bytebank.model.ContaPoupanca
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ContaBancariaTest {

    @Test
    fun `deposito valido aumenta o saldo`() {
        val conta = ContaCorrente(Cliente("Teste", "000.000.000-00"), saldoInicial = 100.0)
        conta.depositar(50.0)
        assertEquals(150.0, conta.consultarSaldo())
    }

    @Test
    fun `deposito de valor negativo ou zero deve ser rejeitado`() {
        val conta = ContaCorrente(Cliente("Teste", "000.000.000-00"), saldoInicial = 100.0)
        assertThrows(ValorInvalidoException::class.java) { conta.depositar(0.0) }
        assertThrows(ValorInvalidoException::class.java) { conta.depositar(-10.0) }
    }

    @Test
    fun `saque maior que saldo e limite deve lancar excecao`() {
        val conta = ContaCorrente(Cliente("Teste", "000.000.000-00"), saldoInicial = 100.0, limiteChequeEspecial = 50.0)
        assertThrows(SaldoInsuficienteException::class.java) { conta.sacar(200.0) }
    }

    @Test
    fun `conta corrente pode sacar usando cheque especial sem ficar abaixo do limite`() {
        val conta = ContaCorrente(Cliente("Teste", "000.000.000-00"), saldoInicial = 100.0, limiteChequeEspecial = 50.0)
        conta.sacar(130.0)
        assertEquals(-30.0, conta.consultarSaldo())
    }

    @Test
    fun `conta poupanca nao permite saldo negativo`() {
        val conta = ContaPoupanca(Cliente("Teste", "000.000.000-00"), saldoInicial = 100.0)
        assertThrows(SaldoInsuficienteException::class.java) { conta.sacar(150.0) }
    }

    @Test
    fun `taxa mensal reduz o saldo da conta corrente`() {
        val conta = ContaCorrente(Cliente("Teste", "000.000.000-00"), saldoInicial = 1000.0)
        conta.aplicarTaxaMensal()
        assertEquals(1000.0 - (1000.0 * conta.percentualTaxa), conta.consultarSaldo(), 0.001)
    }

    @Test
    fun `rendimento aumenta o saldo da poupanca`() {
        val conta = ContaPoupanca(Cliente("Teste", "000.000.000-00"), saldoInicial = 1000.0)
        conta.aplicarRendimento()
        assertEquals(1000.0 + (1000.0 * conta.percentualRendimento), conta.consultarSaldo(), 0.001)
    }

    @Test
    fun `conta investimento bloqueia saque durante a carencia`() {
        val conta = ContaInvestimento(Cliente("Teste", "000.000.000-00"), saldoInicial = 1000.0, carenciaSaques = 1)
        assertThrows(OperacaoNaoSuportadaException::class.java) { conta.sacar(100.0) }
        conta.aplicarRendimento()
        conta.sacar(100.0) // após 1 mês de rendimento, saque liberado
        assertEquals(true, conta.consultarSaldo() > 0)
    }

    @Test
    fun `transferencia move saldo entre contas e registra historico em ambas`() {
        val origem = ContaCorrente(Cliente("Origem", "111.111.111-11"), saldoInicial = 500.0)
        val destino = ContaPoupanca(Cliente("Destino", "222.222.222-22"), saldoInicial = 0.0)

        origem.transferir(200.0, destino)

        assertEquals(300.0, origem.consultarSaldo())
        assertEquals(200.0, destino.consultarSaldo())
        assertEquals(1, origem.historico.size)
        assertEquals(1, destino.historico.size)
    }
}
