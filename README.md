# ByteBank Evolution — Sistema Bancário em Kotlin

Projeto acadêmico (ADS — UNIPÊ, disciplina de Aplicativos Móveis) que implementa,
em Kotlin puro, um sistema bancário orientado a objetos para a fictícia
**ByteBank Evolution**, conforme o exercício "Sistema Bancário ByteBank Evolution".

## Objetivo

Modernizar (na ficção proposta pelo exercício) um sistema bancário legado repleto
de código duplicado e regras espalhadas, substituindo-o por uma solução orientada
a objetos que seja fácil de manter e de estender com novos tipos de conta.

## Conceitos de POO aplicados

| Conceito | Onde aparece |
|---|---|
| Abstração | `ContaBancaria` é uma classe abstrata que define o contrato comum de qualquer conta |
| Herança | `ContaCorrente`, `ContaPoupanca` e `ContaInvestimento` herdam de `ContaBancaria` |
| Polimorfismo | `Banco.processarFechamentoMensal()` aplica taxa ou rendimento sem saber o tipo concreto da conta |
| Encapsulamento | `saldo` só pode ser alterado pelas regras internas da própria conta (`protected set`) |
| Interfaces | `TaxavelMensalmente` e `RendimentoMensal` isolam comportamentos opcionais por tipo de conta |
| Composição | `Cliente` possui uma lista de `ContaBancaria`; `Banco` agrega `Cliente` e `ContaBancaria` |
| Exceções customizadas | `ValorInvalidoException`, `SaldoInsuficienteException`, `OperacaoNaoSuportadaException` |
| Extensibilidade (Open/Closed) | Novos tipos de conta (ex.: `ContaInvestimento`) são adicionados sem alterar código existente |

## Estrutura do projeto

```
APLICATIVOBANCARIO/
├── build.gradle.kts
├── settings.gradle.kts
├── src
│   ├── main/kotlin/com/bytebank
│   │   ├── Main.kt                     # ponto de entrada, cenário de demonstração
│   │   ├── exception/BancoExceptions.kt
│   │   ├── model/
│   │   │   ├── ContaBancaria.kt        # classe abstrata base
│   │   │   ├── ContaCorrente.kt
│   │   │   ├── ContaPoupanca.kt
│   │   │   ├── ContaInvestimento.kt
│   │   │   ├── Cliente.kt
│   │   │   ├── Comportamentos.kt       # interfaces TaxavelMensalmente / RendimentoMensal
│   │   │   └── Transacao.kt            # histórico de operações
│   │   ├── service/Banco.kt            # fachada que orquestra clientes e contas
│   │   └── util/Formatador.kt          # formatação de moeda em pt-BR
│   └── test/kotlin/com/bytebank/ContaBancariaTest.kt
└── gradle/ (wrapper)
```

## Tipos de conta

- **Conta Corrente**: possui limite de cheque especial e cobra taxa mensal de manutenção.
- **Conta Poupança**: sem taxas, rende um percentual mensal sobre o saldo.
- **Conta Investimento**: rendimento maior que a poupança, porém com carência mínima para saque.

Todas herdam de `ContaBancaria`, que garante centralmente que **nenhuma conta
comum fica com saldo negativo**, que **depósitos/saques inválidos são rejeitados**
e que **toda operação é registrada no histórico** da conta.

## Regras de negócio implementadas

- Depósitos e saques com valor `<= 0` lançam `ValorInvalidoException`.
- Saques que excedem o saldo (e o limite de cheque especial, quando existir)
  lançam `SaldoInsuficienteException`.
- Transferências debitam da conta de origem e creditam na conta de destino,
  registrando a operação em ambos os históricos.
- O fechamento mensal (`Banco.processarFechamentoMensal()`) aplica taxa ou
  rendimento de forma polimórfica, conforme a conta implemente
  `TaxavelMensalmente` ou `RendimentoMensal`.

## Como executar

Pré-requisito: JDK 17+.

```bash
./gradlew run
```

## Como rodar os testes

```bash
./gradlew test
```

O projeto inclui uma suíte de testes (JUnit 5) cobrindo depósitos inválidos,
saques com/sem cheque especial, aplicação de taxa/rendimento, carência da
conta investimento e transferência entre contas.

## Saída esperada (resumo)

Ao rodar `./gradlew run`, o programa:

1. Cria 3 clientes e 3 tipos de conta diferentes;
2. Realiza depósitos válidos e inválidos;
3. Realiza saques válidos e com saldo insuficiente;
4. Processa o fechamento mensal (taxas e rendimentos);
5. Realiza uma transferência entre contas;
6. Exibe o extrato geral do banco;
7. Exibe o histórico de operações de cada conta.

---

Atividade desenvolvida para a disciplina de Aplicativos Móveis — ADS/UNIPÊ.

Integrantes 
Pessoal, manda aqui o nome completo e matrícula para documentação:

- Diego Leal Clemente (42182271)
- José Davi Barbosa (45072825)
- Thalyson Chaves Nunes (42066859)
- Nicolas Nery da Silva Feitosa (43663907)
- Makson Douglas Barbosa da Silva (43708935)
- Willian Gabriel Félix Farias (43750001)
- Isacson Joabe Lima Cruz (43652310)
- Luiz Carlos Souza Costa Cavadinha(42782759)
- Luís Gustavo Rocha Gomes De Andrade (42917107)
- Paula Thifanny Gomes Dias (43900119)
- Henrique Santos da Silva (42386098)
- ⁠Eduardo dos Santos Golzio (45660751)
- ⁠Iago Edson Santos Lucena (49955276)
- Flavia Tavares do Nascimento (42031141)
- Priscilla Santos Cahino (43432654)
