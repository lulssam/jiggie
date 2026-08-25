package com.luisamsampaio.jiggie.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.mensagemDeErro
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Gere o estado e a lógica do ecrã Home.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(isLoading = true))

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        carregar()
    }

    /**
     * Vai buscar os dados ao backend e atualiza o estado do ecrã.
     *
     * Mostra um indicador de carregamento enquanto espera,
     * e um erro se algo correr mal.
     */
    fun carregar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // verificar se tem sessão iniciada
                val uid = supabase.auth.currentUserOrNull()?.id
                if (uid == null) {
                    _state.update { it.copy(isLoading = false, error = "No session") }
                    return@launch
                }

                val perfil = supabase.from("dono")
                    .select(
                        Columns.raw("nome, familia(nome)")
                    ) {
                        filter { eq("id", uid) }
                    }
                    .decodeSingle<PerfilDto>()

                _state.update {
                    it.copy(
                        isLoading = false,
                        nome = perfil.nome,
                        familia = perfil.familia?.nome
                    )
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("HomeViewModel.carregar falhou: $erro")
                _state.update { it.copy(isLoading = false, error = mensagemDeErro(erro)) }
            }


        }
    }
}