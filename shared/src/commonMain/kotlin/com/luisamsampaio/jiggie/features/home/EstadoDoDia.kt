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
