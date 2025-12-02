package emv

data class Tlv(
    val tag: String,
    val length: Int,
    val value: String,
    val children: List<Tlv> = emptyList()
)