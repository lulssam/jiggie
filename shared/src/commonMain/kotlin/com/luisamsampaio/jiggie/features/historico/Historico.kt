package com.luisamsampaio.jiggie.features.historico

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.features.home.CaoDto
import com.luisamsampaio.jiggie.features.home.LinhaDeRegisto
import com.luisamsampaio.jiggie.features.home.TipoDeRegisto
import com.luisamsampaio.jiggie.features.home.TituloDeSeccao
import com.luisamsampaio.jiggie.features.home.corDoTipo
import com.luisamsampaio.jiggie.ui.theme.danger
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.primaryContainer
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.primaryLink
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.textBody
import com.luisamsampaio.jiggie.ui.theme.textStrong
import com.luisamsampaio.jiggie.ui.theme.textTertiary

/**
 * Parte visual do ecrã Historico.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun HistoricoScreenContent(
    state: HistoricoUiState,
    nomeDoCao: String,
    onFiltro: (TipoDeRegisto?) -> Unit = {},
    onCarregarMais: () -> Unit = {},
    onRecarregar: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize().background(surface),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 18.dp, end = 18.dp, top = 4.dp)
        ) {
            Cabecalho(nomeDoCao)

            Spacer(Modifier.height(14.dp))

            Filtros(escolhido = state.filtro, onFiltro = onFiltro)

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val centro = Modifier.align(Alignment.Center)
                when {
                    state.isLoading ->
                        CircularProgressIndicator(centro, color = primary)

                    state.error != null && state.dias.isEmpty() ->
                        ErroAoCarregar(
                            state.error,
                            onTentarOutraVez = onRecarregar,
                            modifier = centro
                        )

                    state.dias.isEmpty() ->
                        SemRegistos(nomeDoCao, state.filtro, centro)

                    else -> ListaDeDias(state, onCarregarMais)
                }
            }
        }
    }
}

@Composable
private fun Filtros(
    escolhido: TipoDeRegisto?,
    onFiltro: (TipoDeRegisto?) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filtros.forEach { (tipo, etiqueta) ->
            val ativo = tipo == escolhido
            val forma = RoundedCornerShape(20.dp)

            Row(
                modifier = Modifier
                    .clip(forma)
                    .background(if (ativo) primaryContainer else surface)
                    .border(if (ativo) 2.dp else 1.dp, if (ativo) primary else outline, forma)
                    .selectable(selected = ativo, role = Role.Tab, onClick = { onFiltro(tipo) })
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (tipo != null) {
                    Box(Modifier.size(8.dp).background(corDoTipo(tipo), CircleShape))
                }
                Text(
                    text = etiqueta,
                    style = typography.labelMedium,
                    color = if (ativo) textStrong else textBody
                )
            }
        }
    }
}

/**
 * A lista em si: um cabeçalho preso por dia e as linhas desse dia.
 *
 * Só compõe o que está a vista.*/
@Composable
fun ListaDeDias(
    state: HistoricoUiState,
    onCarregarMais: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        state.dias.forEach { dia ->
            stickyHeader(key = "dia-${dia.data}", contentType = "dia") {
                CabecalhoDoDia(dia.titulo)
            }
            items(dia.registos, key = { it.id }, contentType = { "registo" }) { registo ->
                LinhaDeRegisto(registo)
            }
        }

        if (state.haMais) {
            item(key = "fim", contentType = "fim") {
                FimDaLista(state.error, state.aCarregarMais, onCarregarMais)
            }
        }
    }
}

@Composable
fun FimDaLista(
    erro: String?,
    aCarregarMais: Boolean,
    onCarregarMais: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (erro != null) {
            ErroAoCarregar(erro, onTentarOutraVez = onCarregarMais)
        } else {
            LaunchedEffect(aCarregarMais) {
                if (!aCarregarMais) onCarregarMais()
            }

            CircularProgressIndicator(Modifier.size(20.dp), color = primary, strokeWidth = 2.dp)
        }
    }
}

/**O título de cada dia.*/
@Composable
fun CabecalhoDoDia(titulo: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(surface)
            .padding(top = 16.dp, bottom = 6.dp)
            .semantics { heading() },
    ) {
        TituloDeSeccao(titulo)
    }
}

/**Os filtros, por ordem. O null é o "All" */
private val filtros = listOf(
    null to "All",
    TipoDeRegisto.Passeio to "Walk",
    TipoDeRegisto.Comida to "Food",
    TipoDeRegisto.Agua to "Water",
    TipoDeRegisto.Medicamento to "Medicine",
    TipoDeRegisto.Sintoma to "Symptom",
)

@Composable
fun SemRegistos(
    nomeDoCao: String,
    filtro: TipoDeRegisto?,
    modifier: Modifier = Modifier
) {
    val texto = when {
        nomeDoCao.isEmpty() -> "Add a dog to see its history"
        filtro == null -> "Nothing logged for $nomeDoCao yet"
        else -> {
            val etiqueta = filtros.first { it.first == filtro }.second
            "No ${etiqueta.lowercase()} logged for $nomeDoCao yet"
        }
    }

    Text(
        text = texto,
        style = typography.bodyMedium,
        color = textTertiary,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(horizontal = 24.dp)
    )
}

@Composable
fun ErroAoCarregar(
    erro: String,
    onTentarOutraVez: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = erro,
            style = typography.bodyMedium,
            color = danger,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Try again",
            style = typography.labelMedium,
            color = primaryLink,
            modifier = Modifier.clickable(onClick = onTentarOutraVez).padding(12.dp)
        )
    }
}

@Composable
private fun Cabecalho(nomeDoCao: String) {
    Column {
        Text(text = "History", style = typography.titleLarge, color = primaryDark)
        if (nomeDoCao.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row {
                Text(text = nomeDoCao, style = typography.bodySmall, color = textTertiary)
                // todo ver se é preciso meter a data
            }

        }
    }
}

/**
 * Liga o [HistoricoViewModel] ao [HistoricoScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun HistoricoScreen(
    cao: CaoDto?,
    versaoDosRegistos: Int,
    viewModel: HistoricoViewModel = viewModel { HistoricoViewModel() }
) {
    val state by viewModel.state.collectAsState()

    // corre ao entrar no separador e sempre que o cao ou os registos mudam,
    // o sincronizar decide se há mesmo alguma coisa para ir buscar
    LaunchedEffect(cao?.id, versaoDosRegistos) {
        viewModel.sincronizar(cao?.id, versaoDosRegistos)
    }

    HistoricoScreenContent(
        state = state,
        nomeDoCao = cao?.nome.orEmpty(),
        onFiltro = viewModel::onFiltro,
        onCarregarMais = viewModel::carregarMais,
        onRecarregar = viewModel::recarregar,
    )
}