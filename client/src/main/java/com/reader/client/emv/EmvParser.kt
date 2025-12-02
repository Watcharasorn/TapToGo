package com.reader.client.emv

object EmvParser {

    fun parseResponse(hex: String): EmvResponse {
        val tlvs = TlvParser.parse(hex)
        var aid: String? = null
        var label: String? = null
        var pdol: String? = null

        fun searchTlv(list: List<Tlv>) {
            for (tlv in list) {
                when (tlv.tag.uppercase()) {
                    "4F" -> aid = tlv.value
                    "50" -> label = hexToAscii(tlv.value)
                    "9F38" -> pdol = tlv.value
                }
                if (tlv.children.isNotEmpty()) searchTlv(tlv.children)
            }
        }

        searchTlv(tlvs)
        return EmvResponse(aid, label, pdol)
    }

    private fun hexToAscii(hex: String): String {
        return hex.chunked(2)
            .map { it.toInt(16).toChar() }
            .joinToString("")
    }
}