package com.bytebank

import com.bytebank.exception.OperacaoNaoSuportadaException
import com.bytebank.model.Cliente
import com.bytebank.model.ContaSalario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ContaSalarioTest {

    @Test
    fun `saque dentro do limite gratuito funciona normalmente`() {
        val conta = ContaSalario(Cliente("Teste", "000.000.000-00"), saldoInicial = 1000.0, limiteSaquesGratuitos = 1)
        conta.sacar(100.0)
        assertEquals(900.0, conta.consultarSaldo())
    }

    @Test
    fun `segundo saque no mesmo mes excede o limite gratuito e e bloqueado`() {
        val conta = ContaSalario(Cliente("Teste", "000.000.000-00"), saldoInicial = 1000.0, limiteSaquesGratuitos = 1)
        conta.sacar(100.0)
        assertThrows(OperacaoNaoSuportadaException::class.java) { conta.sacar(50.0) }
    }

    @Test
    fun `reiniciar limite mensal libera novo saque gratuito`() {
        val conta = ContaSalario(Cliente("Teste", "000.000.000-00"), saldoInicial = 1000.0, limiteSaquesGratuitos = 1)
        conta.sacar(100.0)
        conta.reiniciarLimiteMensal()
        conta.sacar(100.0)
        assertEquals(800.0, conta.consultarSaldo())
    }

    @Test
    fun `deposito de salario exige cnpj do empregador`() {
        val conta = ContaSalario(Cliente("Teste", "000.000.000-00"))
        assertThrows(IllegalArgumentException::class.java) { conta.depositarSalario(2000.0, "") }
        conta.depositarSalario(2000.0, "12.345.678/0001-90")
        assertEquals(2000.0, conta.consultarSaldo())
    }
}
