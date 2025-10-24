package com.example.eval2.viewmodel

data class OrderErrors(
    val clientName: String? = null,
    val serviceId: String? = null,
    val scheduleDate: String? = null
)

data class OrderUIState(
    val clientName: String = "",
    val serviceId: Int? = null,
    val scheduleDate: String = "",
    val notes: String = "",
    val photoUri: String? = null,
    val errors: OrderErrors = OrderErrors()
)
