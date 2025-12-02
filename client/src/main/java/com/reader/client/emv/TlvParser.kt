package com.reader.client.emv

data class Tlv(val tag: String, val length: Int, val value: String, val children: List<Tlv> = emptyList()) {

    fun prettyPrint(indent: String = "") {
        println("$indent- Tag: $tag, Length: $length, Value: $value")
        children.forEach { it.prettyPrint(indent + "  ") }
    }

    // --- Helper functions ---
    fun getAid(): String? {
        if (tag == "4F") return value
        return children.mapNotNull { it.getAid() }.firstOrNull()
    }

    fun getLabel(): String? {
        if (tag == "50") return hexToAscii(value)
        return children.mapNotNull { it.getLabel() }.firstOrNull()
    }

    fun getPriority(): Int? {
        if (tag == "87") return value.toInt(16)
        return children.mapNotNull { it.getPriority() }.firstOrNull()
    }

    fun getPdol(): String? {
        if (tag.uppercase() == "9F38") return value
        return children.mapNotNull { it.getPdol() }.firstOrNull()
    }

    fun findTag(tagToFind: String): Tlv? {
        if (tag.uppercase() == tagToFind.uppercase()) return this
        for (child in children) {
            val found = child.findTag(tagToFind)
            if (found != null) return found
        }
        return null
    }

    private fun hexToAscii(hex: String): String {
        val output = StringBuilder("")
        var i = 0
        while (i < hex.length) {
            val str = hex.substring(i, i + 2)
            output.append(str.toInt(16).toChar())
            i += 2
        }
        return output.toString()
    }
}

object TlvParser {
    fun parse(hex: String): List<Tlv> {
        val tlvs = mutableListOf<Tlv>()
        var index = 0
        while (index < hex.length) {
            // Tag
            var tagEnd = index + 2
            if (tagEnd > hex.length) break
            val tagByte1 = hex.substring(index, index + 2).toInt(16)
            if ((tagByte1 and 0x1F) == 0x1F) { // multi-byte tag
                do {
                    tagEnd += 2
                    if (tagEnd > hex.length) break
                    val nextByte = hex.substring(tagEnd - 2, tagEnd).toInt(16)
                } while ((nextByte and 0x80) != 0 && tagEnd < hex.length)
            }
            if (tagEnd > hex.length) break
            val tag = hex.substring(index, tagEnd)
            index = tagEnd

            // Length
            if (index + 2 > hex.length) break
            var len = hex.substring(index, index + 2).toInt(16)
            index += 2
            if ((len and 0x80) != 0) { // long form
                val numBytes = len and 0x7F
                if (index + numBytes * 2 > hex.length) break
                len = hex.substring(index, index + numBytes * 2).toInt(16)
                index += numBytes * 2
            }

            if (index + len * 2 > hex.length) break

            // Value
            val value = hex.substring(index, index + len * 2)
            index += len * 2

            val children = if (isConstructed(tag)) parse(value) else emptyList()
            tlvs.add(Tlv(tag, len, value, children))
        }
        return tlvs
    }

    private fun isConstructed(tag: String): Boolean {
        if (tag.isEmpty()) return false
        val tagByte = tag.substring(0, 2).toInt(16)
        return (tagByte and 0x20) != 0
    }
}