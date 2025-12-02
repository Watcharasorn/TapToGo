package com.reader.client.emv

data class EmvResponse(
    val aid: String? = null,
    val label: String? = null,
    val pdol: String? = null
)