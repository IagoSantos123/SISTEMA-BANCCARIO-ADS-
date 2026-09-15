package com.bytebank.exception

import com.bytebank.util.Formatador

/**
 * Superclasse de todas as exceções de regras de negócio do banco.
 * Permite capturar qualquer violação bancária com um único catch,
 * sem perder a informação específica de cada caso.
 */
sealed class OperacaoBancariaException(message: String) : Exception(message)

class ValorInvalidoException(valor: Double) :
    OperacaoBancariaException(
        "Valor inválido para a operação: ${Formatador.moeda(valor)}. O valor deve ser maior que zero."
    )

class SaldoInsuficienteException(saldoAtual: Double, valorSolicitado: Double) :
    OperacaoBancariaException(
        "Saldo insuficiente para realizar esta operação! " +
            "Saldo atual: ${Formatador.moeda(saldoAtual)} | Valor solicitado: ${Formatador.moeda(valorSolicitado)}"
    )

class ContaInativaException(numeroConta: Int) :
    OperacaoBancariaException("A conta $numeroConta está inativa e não pode realizar operações.")

class OperacaoNaoSuportadaException(mensagem: String) :
    OperacaoBancariaException(mensagem)
