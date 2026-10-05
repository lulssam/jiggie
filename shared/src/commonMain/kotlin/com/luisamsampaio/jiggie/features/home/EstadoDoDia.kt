package com.luisamsampaio.jiggie.features.home

/**
 * O tom de uma pastilha. Fica aqui só o significado — as cores
 * são escolha da UI, para o estado não depender do Compose.
 */
enum class Tom { Neutro, Alerta, Bom, Xixi, Coco }

/** Uma etiqueta pequena de fundo colorido: "DUE 6:00 PM", "PEE", "ALL GIVEN". */
data class Pastilha(val texto: String, val tom: Tom)

/**
 * O resumo de hoje para o cão selecionado.
 *
 * Os valores por omissão são o estado vazio —
 * um cão acabado de criar, sem nada registado.
 */
data class EstadoDoDia(
    val medicamentos: String = "None scheduled",
    val medPastilha: Pastilha = Pastilha("NONE", Tom.Neutro),
    val medContagem: String = "Tap to add",
    val passeio: String = "No walk logged",
    val passeioEtiquetas: List<Pastilha> = listOf(Pastilha("NONE YET", Tom.Neutro)),
    val comida: String = "No meals yet",
    val comidaContagem: String = "0/3",
    val agua: String = "No water logged",
    val aguaTotal: String = "0 ml",
)

/** O tipo de registo. Só isto — a cor do ponto é escolha da UI. */
enum class TipoDeRegisto { Passeio, Comida, Agua, Medicamento, Sintoma }

/**
 * Uma linha da actividade recente.
 *
 * @property hora Já formatada ("8:15 AM") — quem sabe formatar é o ViewModel.
 * * @property pastilhas O que aparece à direita da linha ("PEE", "POOP"). Vazia nos outros tipos.
 */
data class Registo(
    val id: String,
    val hora: String,
    val titulo: String,
    val subtitulo: String,
    val tipo: TipoDeRegisto,
    val pastilhas: List<Pastilha> = emptyList()
)

/**
 * O sintoma mais recento do cão. Null quando não há*/
data class Sinalizado(
    val titulo: String,
    val subtitulo: String
)
internal fun pastilhasDoPasseio(xixi: Boolean, coco: Boolean): List<Pastilha> = buildList {
    if (xixi) add(Pastilha("PEE", Tom.Xixi))
    if (coco) add(Pastilha("POOP", Tom.Coco))
}

internal fun etiquetasDoPasseio(passeio: PasseioDto?): List<Pastilha> {
    if (passeio == null) return listOf(Pastilha("NONE YET", Tom.Neutro))
    return pastilhasDoPasseio(passeio.xixi, passeio.coco)
        .ifEmpty { listOf(Pastilha("NO OUTPUT", Tom.Neutro)) }
}