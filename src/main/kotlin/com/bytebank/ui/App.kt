@file:OptIn(ExperimentalMaterial3Api::class)

package com.bytebank.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.bytebank.util.Formatador
import kotlinx.coroutines.delay

@Composable
fun App(controller: BancoController) {
    Scaffold(
        topBar = { BancoTopBar(controller.nomeBanco) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Row(Modifier.fillMaxSize().padding(padding)) {
            AccountSidebar(controller, Modifier.width(300.dp).fillMaxHeight())
            Box(Modifier.fillMaxHeight().width(1.dp).background(MaterialTheme.colorScheme.outline))
            val conta = controller.contaSelecionada
            if (conta != null) {
                AccountDetail(controller, conta, Modifier.weight(1f).fillMaxHeight())
            } else {
                Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma conta cadastrada.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun BancoTopBar(nomeBanco: String) {
    TopAppBar(
        title = {
            Column {
                Text(nomeBanco, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text(
                    "Sistema bancário orientado a objetos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

// ---------------------------------------------------------------------
// Barra lateral: lista de contas + ação de fechamento mensal
// ---------------------------------------------------------------------

@Composable
private fun AccountSidebar(controller: BancoController, modifier: Modifier = Modifier) {
    Column(modifier.background(MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(20.dp)) {
            Text("Contas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "${controller.contas.size} conta(s) cadastrada(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(controller.contas, key = { it.numero }) { conta ->
                AccountListItem(
                    conta = conta,
                    selecionado = conta.numero == controller.numeroSelecionado,
                    onClick = { controller.selecionar(conta.numero) }
                )
            }
        }
        Button(
            onClick = { controller.processarFechamentoMensal() },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Processar fechamento mensal")
        }
    }
}

@Composable
private fun AccountListItem(conta: ContaResumo, selecionado: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selecionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selecionado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                conta.tipoConta.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 0.08.em
            )
            Spacer(Modifier.height(2.dp))
            Text(conta.titular, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text("Conta ${conta.numero}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(6.dp))
            Text(Formatador.moeda(conta.saldo), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

// ---------------------------------------------------------------------
// Painel principal: saldo, operações e histórico da conta selecionada
// ---------------------------------------------------------------------

@Composable
private fun AccountDetail(controller: BancoController, conta: ContaResumo, modifier: Modifier = Modifier) {
    Column(modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        MensagemBanner(controller.mensagem, onDismiss = controller::limparMensagem)

        SaldoHeader(conta)

        Spacer(Modifier.height(24.dp))
        Text("Operações", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            DepositoCard(controller, Modifier.weight(1f))
            SaqueCard(controller, Modifier.weight(1f))
            TransferenciaCard(controller, conta, Modifier.weight(1f))
        }

        Spacer(Modifier.height(28.dp))
        Text("Histórico de operações", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        HistoricoList(conta.historico)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun MensagemBanner(mensagem: Mensagem?, onDismiss: () -> Unit) {
    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            delay(4000)
            onDismiss()
        }
    }
    AnimatedVisibility(
        visible = mensagem != null,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        if (mensagem != null) {
            val erro = mensagem is Mensagem.Erro
            Surface(
                color = if (erro) BankColors.DangerContainer else BankColors.SuccessContainer,
                contentColor = if (erro) BankColors.Danger else BankColors.Success,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.height(8.dp).width(8.dp)
                            .background(if (erro) BankColors.Danger else BankColors.Success, RoundedCornerShape(50))
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(mensagem.texto, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SaldoHeader(conta: ContaResumo) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(Modifier.padding(24.dp).fillMaxWidth()) {
            Text(
                conta.tipoConta.uppercase(),
                color = Color.White.copy(alpha = .75f),
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 0.1.em
            )
            Text(conta.titular, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Conta ${conta.numero}", color = Color.White.copy(alpha = .75f), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(18.dp))
            Text("Saldo atual", color = Color.White.copy(alpha = .75f), style = MaterialTheme.typography.labelMedium)
            Text(
                Formatador.moeda(conta.saldo),
                color = Color.White,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold
            )
            conta.detalhe?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = Color.White.copy(alpha = .85f), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ---------------------------------------------------------------------
// Cartões de operação: depósito, saque e transferência
// ---------------------------------------------------------------------

@Composable
private fun OperationCard(titulo: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ValorField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Valor (R$)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DepositoCard(controller: BancoController, modifier: Modifier = Modifier) {
    var valorTexto by remember(controller.numeroSelecionado) { mutableStateOf("") }
    OperationCard("Depósito", modifier) {
        ValorField(valorTexto) { valorTexto = it }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = {
                controller.depositar(parseValorOuInvalido(valorTexto))
                valorTexto = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Depositar") }
    }
}

@Composable
private fun SaqueCard(controller: BancoController, modifier: Modifier = Modifier) {
    var valorTexto by remember(controller.numeroSelecionado) { mutableStateOf("") }
    OperationCard("Saque", modifier) {
        ValorField(valorTexto) { valorTexto = it }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = {
                controller.sacar(parseValorOuInvalido(valorTexto))
                valorTexto = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Sacar") }
    }
}

@Composable
private fun TransferenciaCard(controller: BancoController, contaAtual: ContaResumo, modifier: Modifier = Modifier) {
    var valorTexto by remember(controller.numeroSelecionado) { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val destinos = controller.contas.filter { it.numero != contaAtual.numero }
    var destino by remember(controller.numeroSelecionado) { mutableStateOf(destinos.firstOrNull()) }

    OperationCard("Transferência", modifier) {
        ValorField(valorTexto) { valorTexto = it }
        Spacer(Modifier.height(10.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = destino?.let { "Conta ${it.numero} · ${it.titular}" } ?: "Sem conta disponível",
                onValueChange = {},
                readOnly = true,
                label = { Text("Conta destino") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                destinos.forEach { d ->
                    DropdownMenuItem(
                        text = { Text("Conta ${d.numero} · ${d.titular}") },
                        onClick = {
                            destino = d
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = {
                destino?.let {
                    controller.transferir(parseValorOuInvalido(valorTexto), it.numero)
                    valorTexto = ""
                }
            },
            enabled = destino != null,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Transferir") }
    }
}

// ---------------------------------------------------------------------
// Histórico
// ---------------------------------------------------------------------

@Composable
private fun HistoricoList(historico: List<String>) {
    if (historico.isEmpty()) {
        Text(
            "Nenhuma operação registrada ainda.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(vertical = 4.dp)) {
            historico.forEachIndexed { index, linha ->
                Text(
                    linha,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
                if (index != historico.lastIndex) {
                    Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = .5f)))
                }
            }
        }
    }
}
