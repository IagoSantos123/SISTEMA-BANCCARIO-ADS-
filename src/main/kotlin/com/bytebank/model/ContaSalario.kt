class ContaSalario(
    numero: String,
    titular: String,
    saldoInicial: Double = 0.0
) : Conta(numero, titular, saldoInicial) {

    private var saquesRealizadosMensal = 0
    private val limiteSaquesGratuitos = 1 // Regra padrão comum de contas salário

    override fun sacar(valor: Double): Boolean {
        if (saquesRealizadosMensal >= limiteSaquesGratuitos) {
            throw IllegalStateException("Limite de saques gratuitos da Conta Salário atingido.")
        }

        val sucesso = super.sacar(valor)
        if (sucesso) {
            saquesRealizadosMensal++
        }
        return sucesso
    }

    // Exemplo de regra adicional: Conta salário geralmente não aceita depósito de qualquer origem (só do empregador)
    fun depositarDoEmpregador(valor: Double, cnpjEmpregador: String) {
        // Aqui você poderia validar o CNPJ da empresa autorizada
        super.depositar(valor)
    }
}
