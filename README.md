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

## Interface gráfica (Compose Desktop)

Além da versão de console exigida pelo exercício, o projeto inclui uma interface
gráfica real em Kotlin, usando **Compose Multiplatform for Desktop**, que consome
exatamente as mesmas classes de domínio (`Banco`, `ContaBancaria` e subclasses) —
nenhuma regra de negócio é duplicada entre console e UI.

A tela mostra a lista de contas cadastradas, o saldo em destaque da conta
selecionada, formulários de depósito/saque/transferência e o histórico de
operações, tudo reagindo em tempo real às regras já implementadas (uma tentativa
de saque acima do limite, por exemplo, aparece como erro vindo da própria
`ContaBancaria`, não de uma validação duplicada na tela).

```bash
./gradlew run
```

> Em máquinas Linux com JDK **headless** (sem suporte gráfico), é preciso um JDK
> completo (ex.: `sudo apt install openjdk-17-jdk`, ou um JDK como o Eclipse Temurin)
> para que a janela consiga abrir.

## Estrutura do projeto

```
APLICATIVOBANCARIO/
├── build.gradle.kts
├── settings.gradle.kts
├── src
│   ├── main/kotlin/com/bytebank
│   │   ├── Main.kt                     # ponto de entrada da versão console
│   │   ├── DemoData.kt                 # cenário de demonstração compartilhado (console + UI)
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
│   │   ├── util/Formatador.kt          # formatação de moeda em pt-BR
│   │   └── ui/                         # interface gráfica (Compose Desktop)
│   │       ├── MainUi.kt               # ponto de entrada da UI
│   │       ├── BancoController.kt      # ponte entre a UI e o domínio bancário
│   │       ├── Theme.kt                # paleta e tipografia
│   │       └── App.kt                  # telas (sidebar, saldo, operações, histórico)
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

Pré-requisito: JDK 17+ **com suporte gráfico** (não headless).

```bash
./gradlew run          # abre a interface gráfica (Compose Desktop)
./gradlew runConsole    # executa a versão de console original do exercício
```

## Como rodar os testes

```bash
./gradlew test
```

O projeto inclui uma suíte de testes (JUnit 5) cobrindo depósitos inválidos,
saques com/sem cheque especial, aplicação de taxa/rendimento, carência da
conta investimento e transferência entre contas.

## Saída esperada (resumo)

Ao rodar `./gradlew runConsole`, o programa:

1. Cria 3 clientes e 3 tipos de conta diferentes;
2. Realiza depósitos válidos e inválidos;
3. Realiza saques válidos e com saldo insuficiente;
4. Processa o fechamento mensal (taxas e rendimentos);
5. Realiza uma transferência entre contas;
6. Exibe o extrato geral do banco;
7. Exibe o histórico de operações de cada conta.

Ao rodar `./gradlew run`, as mesmas operações ficam disponíveis interativamente
pela interface gráfica, com o mesmo cenário inicial de contas/clientes.

---

Atividade desenvolvida para a disciplina de Aplicativos Móveis — ADS/UNIPÊ.
