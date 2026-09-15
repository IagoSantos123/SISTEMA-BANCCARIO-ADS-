package com.bytebank.ui

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.bytebank.DemoData

fun main() = application {
    val controller = remember { BancoController(DemoData.criarBancoDemonstracao()) }
    val windowState = rememberWindowState(
        width = 1180.dp,
        height = 760.dp,
        position = WindowPosition(Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "ByteBank Evolution",
        state = windowState
    ) {
        ByteBankTheme {
            App(controller)
        }
    }
}
