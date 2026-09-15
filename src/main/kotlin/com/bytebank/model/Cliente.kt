package com.bytebank.model

/**
 * Representa o correntista. Mantém a lista das contas que possui, permitindo
 * que um mesmo cliente tenha, por exemplo, conta corrente e poupança.
 */
class Cliente(
    val nome: String,
    val cpf: String
) {
    private val _contas = mutableListOf<ContaBancaria>()
    val contas: List<ContaBancaria>
        get() = _contas.toList()

    internal fun vincularConta(conta: ContaBancaria) {
        _contas.add(conta)
    }

    override fun toString(): String = "$nome (CPF: $cpf)"
}
