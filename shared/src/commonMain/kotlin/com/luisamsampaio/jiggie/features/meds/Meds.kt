package com.luisamsampaio.jiggie.features.meds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.features.home.CaoDto
import com.luisamsampaio.jiggie.features.home.TituloDeSeccao
import com.luisamsampaio.jiggie.features.log.ChipEscolha
import com.luisamsampaio.jiggie.features.meds.ui.resumo
import com.luisamsampaio.jiggie.ui.BotaoPrincipal
import com.luisamsampaio.jiggie.ui.CampoTexto
import com.luisamsampaio.jiggie.ui.bordaTracejada
import com.luisamsampaio.jiggie.ui.theme.danger
import com.luisamsampaio.jiggie.ui.theme.dangerBorder
import com.luisamsampaio.jiggie.ui.theme.divider
import com.luisamsampaio.jiggie.ui.theme.inputBorder
import com.luisamsampaio.jiggie.ui.theme.med
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.outlineStrong
import com.luisamsampaio.jiggie.ui.theme.plexMono
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.success
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.textBody
import com.luisamsampaio.jiggie.ui.theme.textDisabled
import com.luisamsampaio.jiggie.ui.theme.textStrong
import com.luisamsampaio.jiggie.ui.theme.textTertiary
import io.github.jan.supabase.realtime.Column
import kotlinx.datetime.DayOfWeek

/**
 * Parte visual do ecrã Meds.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun MedsScreenContent(
    state: MedsUiState,
    nomeDoCao: String,
    onNome: (String) -> Unit = {},
    onDose: (String) -> Unit = {},
    onOpcao: (OpcaoDeFrequencia) -> Unit = {},
    onDia: (DayOfWeek) -> Unit = {},
    onVezesPorDia: (Int) -> Unit = {},
    onAdicionar: () -> Unit = {},
    onArquivar: (String) -> Unit = {},
    onAlternar: (String, TomaUi) -> Unit = { _, _ -> },
    onTentarOutraVez: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize().background(surface),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Cabecalho(nomeDoCao)

            when {
                nomeDoCao.isEmpty() ->
                    Text(
                        "Add a dog to set up its medicine.",
                        style = typography.bodyMedium,
                        color = textTertiary
                    )

                state.isLoading ->
                    CircularProgressIndicator(
                        Modifier.align(Alignment.CenterHorizontally),
                        color = primary
                    )

                else -> {
                    state.error?.let { AvisoDeErro(it, onTentarOutraVez) }

                    // o key liga cada cartão ao seu medicamento: ao remover um,
                    // os outros não herdam o estado do vizinhos
                    state.medicamentos.forEach { m ->
                        key(m.id) {
                            CartaoDoMedicamento(
                                medicamento = m,
                                onArquivar = { onArquivar(m.id) },
                                onAlternar = { toma -> onAlternar(m.id, toma) }
                            )
                        }
                    }

                    CartaoAdicionar(
                        formulario = state.formulario,
                        onNome = onNome,
                        onDose = onDose,
                        onOpcao = onOpcao,
                        onDia = onDia,
                        onVezesPorDia = onVezesPorDia,
                        onAdicionar = onAdicionar
                    )
                }
            }
        }
    }
}

@Composable
fun CartaoAdicionar(
    formulario: FormularioUi,
    onNome: (String) -> Unit,
    onDose: (String) -> Unit,
    onOpcao: (OpcaoDeFrequencia) -> Unit,
    onDia: (DayOfWeek) -> Unit,
    onVezesPorDia: (Int) -> Unit,
    onAdicionar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .bordaTracejada(cor = outlineStrong, raio = 13.dp)
            .padding(13.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TituloDeSeccao("ADD MEDICINE")

        CampoTexto(
            formulario.nome,
            onNome,
            "Name (e.g. Galliprant)",
            KeyboardType.Text,
            estilo = typography.bodyMedium
        )

        CampoTexto(
            formulario.dose,
            onDose,
            "Dosage (e.g. 60mg)",
            KeyboardType.Text,
            estilo = typography.bodyMedium
        )

        Rotulo("How often")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            opcoes.forEach { (opcao, texto) ->
                ChipEscolha(
                    texto = texto,
                    ativo = opcao == formulario.opcao,
                    onClick = { onOpcao(opcao) },
                    raio = 9.dp
                )
            }
        }

        if (formulario.opcao == OpcaoDeFrequencia.DiasDaSemana) {
            SeletorDeDias(formulario.dias, onDia)
        }

        Rotulo("Times per dose day")
        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            (1..3).forEach { vezes ->
                ChipEscolha(
                    texto = "$vezes×",
                    ativo = vezes == formulario.vezesPorDia,
                    onClick = { onVezesPorDia(vezes) },
                    raio = 9.dp
                )
            }
        }

        Text(
            text = formulario.resumo(),
            fontFamily = plexMono(),
            fontSize = 11.sp,
            color = textTertiary
        )

        BotaoPrincipal(
            texto = "+ Add to schedule", activo = formulario.podeAdicionar, onClique = onAdicionar
        )
    }
}

/**
 * Os sete dias, de segunda a domingo. É escolha múltipla, por isso usa
 * toggleable com Role.Checkbox: o leitor de ecrã diz "marcado" ou "não marcado".
 */
@Composable
fun SeletorDeDias(escolhidos: Set<DayOfWeek>, onDia: (DayOfWeek) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        DayOfWeek.entries.forEach { dia ->
            val ativo = dia in escolhidos
            val forma = RoundedCornerShape(9.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(forma)
                    .background(if (ativo) med else Color.White)
                    .border(1.dp, if (ativo) med else inputBorder, forma)
                    .toggleable(
                        value = ativo,
                        role = Role.Checkbox,
                        onValueChange = { onDia(dia) }),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dia.name.take(1),
                    fontFamily = plexMono(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (ativo) Color.White else textBody
                )
            }
        }
    }
}

private val opcoes = listOf(
    OpcaoDeFrequencia.Diaria to "Every day",
    OpcaoDeFrequencia.DiaSimDiaNao to "Every other day",
    OpcaoDeFrequencia.DiasDaSemana to "Specific days",
)

@Composable
fun Rotulo(texto: String) {
    Text(texto, fontSize = 11.sp, color = textTertiary)
}

/** Um medicamento: nome, frequencia, a semana e as tomas de hoje*/
@Composable
fun CartaoDoMedicamento(
    medicamento: MedicamentoUi,
    onArquivar: () -> Unit,
    onAlternar: (TomaUi) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, outline, RoundedCornerShape(13.dp))
            .padding(13.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Row {
            Column(Modifier.weight(1f)) {
                Text(
                    text = medicamento.nome,
                    style = typography.titleMedium,
                    color = textStrong
                )
                Text(
                    text = medicamento.detalhe,
                    style = typography.bodyMedium,
                    color = textTertiary
                )
            }

            BotaoRemover(onArquivar)
        }

        TiraDaSemana(medicamento.semana)

        val proxima = medicamento.proxima
        if (proxima != null) {
            Text(proxima, style = typography.bodySmall, color = textTertiary)
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                medicamento.tomas.forEach { toma ->
                    ChipEscolha(
                        texto = "${if (toma.dada) "✓" else "○"} ${toma.texto}",
                        ativo = toma.dada,
                        onClick = { onAlternar(toma) },
                        corAtiva = success,
                        raio = 9.dp
                    )
                }
            }
        }
    }
}

/** M T W T F S S: os dias de toma a roxo, hoje preenchido; hoje sem toma leva um aro. */
@Composable
fun TiraDaSemana(dias: List<DiaUi>) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        dias.forEach { dia ->
            val forma = RoundedCornerShape(7.dp)
            val (fundo, letra) = when {
                dia.toca && dia.hoje -> med to Color.White
                dia.toca -> med.copy(alpha = 0.12f) to med
                else -> divider to textDisabled
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(26.dp)
                    .clip(forma)
                    .background(fundo)
                    .then(
                        if (dia.hoje && !dia.toca) Modifier.border(
                            1.5.dp,
                            med,
                            forma
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    dia.letra,
                    fontFamily = plexMono(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold, color = letra
                )
            }
        }
    }

}

@Composable
fun BotaoRemover(onClick: () -> Unit) {
    val forma = RoundedCornerShape(8.dp)
    Text(
        text = "REMOVE",
        fontFamily = plexMono(),
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = danger,
        modifier = Modifier
            .clip(forma)
            .border(1.dp, dangerBorder, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun AvisoDeErro(x0: String, x1: () -> Unit) {
}

@Composable
fun Cabecalho(nomeDoCao: String) {
    Column {
        Text(
            text = "Medicine",
            style = typography.titleMedium,
            color = textStrong
        )

        if (nomeDoCao.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "$nomeDoCao · schedule & doses",
                style = typography.bodySmall,
                color = textTertiary
            )
        }
    }
}

/**
 * Liga o [MedsViewModel] ao [MedsScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun MedsScreen(
    cao: CaoDto?,
    versaoDosRegistos: Int,
    viewModel: MedsViewModel = viewModel { MedsModulo.viewModel() }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(cao?.id, versaoDosRegistos) {
        viewModel.sincronizar(cao?.id, versaoDosRegistos)
    }

    MedsScreenContent(
        state = state,
        nomeDoCao = cao?.nome.orEmpty(),
        onNome = viewModel::onNome,
        onDose = viewModel::onDose,
        onOpcao = viewModel::onOpcao,
        onDia = viewModel::onDia,
        onVezesPorDia = viewModel::onVezesPorDia,
        onAdicionar = viewModel::adicionar,
        onArquivar = viewModel::arquivar,
        onTentarOutraVez = viewModel::carregar
    )
}