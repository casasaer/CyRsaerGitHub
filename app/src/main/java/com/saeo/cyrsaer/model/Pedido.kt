package com.saeo.cyrsaer.model

import com.google.firebase.firestore.DocumentId

data class Pedido(
    @DocumentId
    val id: String? = null,
    val mesa: String = "",
    val fecha: String = "",
    val estado: String = "",
    val usuario: String = "",
    val platos: List<Plato>? = null
)

