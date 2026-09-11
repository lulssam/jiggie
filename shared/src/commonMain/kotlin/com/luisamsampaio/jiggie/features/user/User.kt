package com.luisamsampaio.jiggie.features.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.ui.CodigoComCopiar
import com.luisamsampaio.jiggie.ui.theme.danger
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.plexMono
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.primaryContainer
import com.luisamsampaio.jiggie.ui.theme.primaryLink
import com.luisamsampaio.jiggie.ui.theme.textStrong
import com.luisamsampaio.jiggie.ui.theme.textTertiary

/**
 * Parte visual do ecrã User.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun UserScreenContent(
    state: UserUiState,
    onSair: () -> Unit
) {
    if (state.isLoading) {
        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column {
        CabecalhoDaConta(state)
        CartaoDaFamilia(state)
    }
}

/**
 * Liga o [UserViewModel] ao [UserScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun UserScreen(
    viewModel: UserViewModel = viewModel { UserViewModel() },
    onSair: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // cada vez que o pop up abre, rele o viewmodel
    LaunchedEffect(Unit) {
        viewModel.carregar()
    }

    LaunchedEffect(state.logout) {
        if (state.logout) onSair()
    }

    UserScreenContent(
        state = state,
        onSair = viewModel::sair
    )
}


@Composable
private fun CabecalhoDaConta(
    state: UserUiState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.nome.firstOrNull()?.uppercase().orEmpty(),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Column(
            Modifier.weight(1f)
        ) {
            Text(
                text = state.nome,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textStrong
            )
            Text(
                text = state.email,
                style = MaterialTheme.typography.bodySmall,
                color = textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = state.papel,
            fontFamily = plexMono(),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            color = primaryLink,
            modifier = Modifier
                .background(primaryContainer, RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun CartaoDaFamilia(
    state: UserUiState
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, outline, RoundedCornerShape(13.dp))
            .padding(horizontal = 14.dp, vertical = 13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = state.nomeFamilia,
                style = MaterialTheme.typography.titleSmall,
                color = textStrong
            )

            Text(
                text = if (state.membros == 1) "1 member" else "${state.membros} members",
                fontSize = 11.sp,
                color = textTertiary
            )
        }

        Spacer(Modifier.height(9.dp))
        CodigoComCopiar(state.codigoFamilia, tamanhoCodigo = 15.sp)
    }
}