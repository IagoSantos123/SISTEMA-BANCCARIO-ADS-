package com.bytebank.model

import com.bytebank.exception.OperacaoNaoSuportadaException

/**
 * Conta Salário: pensada para receber o pagamento do empregador. Não cobra taxa
 * de manutenção nem rende, mas libera apenas um número limitado de saques
 * gratuitos por mês — extras ficam bloqueados até o próximo fechamento mensal.
 *
 * Ideia original de conta proposta pela colega Flavia Tavares Nascimento,
 * reimplementada aqui sobre a hierarquia real de [ContaBancaria] do projeto.
 */
class ContaSalario(
    cliente: Cliente,
    saldoInicial: Double = 0.0,
    private val limiteSaquesGratuitos: Int = 1
) : ContaBancaria(gerarNumeroConta(), cliente, saldoInicial), LimiteMensalReiniciavel {

    init {
        cliente.vincularConta(this)
    }

    override val tipoConta: String = "Conta Salário"

    private var saquesRealizadosNoMes: Int = 0

    override fun sacar(valor: Double) {
        if (saquesRealizadosNoMes >= limiteSaquesGratuitos) {
            throw OperacaoNaoSuportadaException(
                "Conta Salário $numero já utilizou seu(s) $limiteSaquesGratuitos saque(s) gratuito(s) do mês."
            )
        }
        super.sacar(valor)
        saquesRealizadosNoMes++
    }

    override fun reiniciarLimiteMensal() {
        saquesRealizadosNoMes = 0
    }

    /** Depósito identificado como pagamento do empregador (reaproveita a validação padrão de depósito). */
    fun depositarSalario(valor: Double, cnpjEmpregador: String) {
        require(cnpjEmpregador.isNotBlank()) { "Informe o CNPJ do empregador para o depósito de salário." }
        depositar(valor)
    }
}
