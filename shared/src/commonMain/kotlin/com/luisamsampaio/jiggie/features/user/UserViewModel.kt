package com.luisamsampaio.jiggie.features.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.features.codigo.buscarCodigoConvite
import com.luisamsampaio.jiggie.mensagemDeErro
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Gere o estado e a lógica do ecrã User.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class UserViewModel : ViewModel() {

    private val _state = MutableStateFlow(UserUiState(isLoading = true))

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<UserUiState> = _state.asStateFlow()

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
                val user = supabase.auth.currentUserOrNull()
                if (user == null) {
                    _state.update { it.copy(error = "Not logged in") }
                    return@launch
                }

                coroutineScope {
                    val perfil = async {
                        supabase.from("dono")
                            .select(Columns.raw("nome, papel, familia(nome)")) {
                                filter { eq("id", user.id) }
                            }.decodeSingle<UserDto>()
                    }

                    // rls so mostra membros da familia
                    val membros = async {
                        supabase.from("dono")
                            .select(Columns.list("id")) // contar linhas é contar os membros
                            .decodeList<IdDto>()
                    }

                    val caes = async {
                        supabase.from("cao")
                            .select(Columns.list("nome"))
                            .decodeList<NomeDto>()
                    }

                    val p = perfil.await()
                    val totalMembros = membros.await().size
                    val nomesDosCaes = caes.await().map { c -> c.nome }
                    val codigo = if (p.familia != null) buscarCodigoConvite() else ""

                    _state.update {
                        it.copy(
                            nome = p.nome,
                            email = user.email.orEmpty(),
                            papel = if (p.papel == "dono") "OWNER" else "MEMBER",
                            nomeFamilia = p.familia?.nome.orEmpty(),
                            codigoFamilia = codigo,
                            membros = totalMembros,
                            caes = nomesDosCaes,
                        )
                    }
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("UserViewModel.carregar falhou: $erro")
                _state.update { it.copy(error = mensagemDeErro(erro)) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }

        }
    }

    fun sair() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _state.update { it.copy(logout = true) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("UserViewModel.sair falhou: $erro")
                _state.update { it.copy(error = mensagemDeErro(erro)) }
            }
        }
    }

}