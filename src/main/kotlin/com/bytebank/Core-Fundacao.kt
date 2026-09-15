// Classe que representa um cliente do banco
class Cliente(
    val nome: String,
    val cpf: String
) {

    fun exibirDadosCliente() {
        println("Nome: $nome")
        println("CPF: $cpf")
    }
}


// Data class responsável por guardar uma operação realizada
data class Operacao(
    val tipo: String,
    val valor: Double,
    val descricao: String
)


// Classe principal que será reutilizada pelos diferentes tipos de conta
abstract class ContaBancaria(
    val numeroConta: Int,
    val cliente: Cliente,
    saldoInicial: Double
) {

    protected var saldo: Double = saldoInicial

    protected val historico = mutableListOf<Operacao>()


    // Realiza depósito
    fun depositar(valor: Double) {

        if (valor > 0) {

            saldo += valor

            val operacao = Operacao(
                "Depósito",
                valor,
                "Depósito realizado com sucesso"
            )

            historico.add(operacao)

            println("Depósito realizado com sucesso!")

        } else {

            println("Valor de depósito inválido!")
        }
    }


    // Realiza saque
    fun sacar(valor: Double) {

        if (valor <= 0) {

            println("Valor de saque inválido!")

        } else if (valor <= saldo) {

            saldo -= valor

            val operacao = Operacao(
                "Saque",
                valor,
                "Saque realizado com sucesso"
            )

            historico.add(operacao)

            println("Saque realizado com sucesso!")

        } else {

            println("Saldo insuficiente!")
        }
    }


    // Consulta o saldo
    fun consultarSaldo() {

        println("Saldo atual: R$ $saldo")
    }


    // Mostra os dados da conta
    fun exibirDados() {

        println("==============================")
        println("Cliente: ${cliente.nome}")
        println("CPF: ${cliente.cpf}")
        println("Conta: $numeroConta")
        println("Tipo da conta: ${tipoConta()}")
        println("Saldo: R$ $saldo")
        println("==============================")
    }


    // Exibe as operações realizadas
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


    // Cada tipo de conta deverá informar seu próprio nome
    abstract fun tipoConta(): String


    // Cada tipo de conta poderá ter sua própria regra
    abstract fun aplicarRegraEspecifica()
}
