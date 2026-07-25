package com.example.movix.domain.utils

import java.security.MessageDigest

object Md5Crypt {
    private const val MAGIC = "$1$"
    private const val ITOA64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    private fun to64(v: Int, n: Int): String {
        var value = v
        var count = n
        val sb = StringBuilder()
        while (--count >= 0) {
            sb.append(ITOA64[value and 0x3f])
            value = value ushr 6
        }
        return sb.toString()
    }

    fun md5Crypt(password: String, saltInput: String, magicInput: String? = null): String {
        val magic = magicInput ?: MAGIC
        var salt = saltInput

        if (salt.startsWith(magic)) {
            salt = salt.substring(magic.length)
        }

        if (salt.contains("$")) {
            salt = salt.split("$")[0]
        }

        if (salt.length > 8) {
            salt = salt.substring(0, 8)
        }

        val pwBytes = password.toByteArray(Charsets.UTF_8)
        val saltBytes = salt.toByteArray(Charsets.UTF_8)
        val magicBytes = magic.toByteArray(Charsets.UTF_8)

        val md = MessageDigest.getInstance("MD5")
        md.update(pwBytes)
        md.update(magicBytes)
        md.update(saltBytes)

        val md2 = MessageDigest.getInstance("MD5")
        md2.update(pwBytes)
        md2.update(saltBytes)
        md2.update(pwBytes)
        var finalBytes = md2.digest()

        var pl = pwBytes.size
        while (pl > 0) {
            val len = if (pl > 16) 16 else pl
            md.update(finalBytes, 0, len)
            pl -= 16
        }

        var i = pwBytes.size
        while (i != 0) {
            if ((i and 1) != 0) {
                md.update(0.toByte())
            } else {
                md.update(pwBytes[0])
            }
            i = i ushr 1
        }

        finalBytes = md.digest()

        for (j in 0 until 1000) {
            val mdIter = MessageDigest.getInstance("MD5")
            if ((j and 1) != 0) {
                mdIter.update(pwBytes)
            } else {
                mdIter.update(finalBytes, 0, 16)
            }

            if (j % 3 != 0) {
                mdIter.update(saltBytes)
            }

            if (j % 7 != 0) {
                mdIter.update(pwBytes)
            }

            if ((j and 1) != 0) {
                mdIter.update(finalBytes, 0, 16)
            } else {
                mdIter.update(pwBytes)
            }
            finalBytes = mdIter.digest()
        }

        val sb = StringBuilder()
        sb.append(magic)
        sb.append(salt)
        sb.append("$")

        val b0 = finalBytes[0].toInt() and 0xff
        val b1 = finalBytes[1].toInt() and 0xff
        val b2 = finalBytes[2].toInt() and 0xff
        val b3 = finalBytes[3].toInt() and 0xff
        val b4 = finalBytes[4].toInt() and 0xff
        val b5 = finalBytes[5].toInt() and 0xff
        val b6 = finalBytes[6].toInt() and 0xff
        val b7 = finalBytes[7].toInt() and 0xff
        val b8 = finalBytes[8].toInt() and 0xff
        val b9 = finalBytes[9].toInt() and 0xff
        val b10 = finalBytes[10].toInt() and 0xff
        val b11 = finalBytes[11].toInt() and 0xff
        val b12 = finalBytes[12].toInt() and 0xff
        val b13 = finalBytes[13].toInt() and 0xff
        val b14 = finalBytes[14].toInt() and 0xff
        val b15 = finalBytes[15].toInt() and 0xff

        sb.append(to64((b0 shl 16) or (b6 shl 8) or b12, 4))
        sb.append(to64((b1 shl 16) or (b7 shl 8) or b13, 4))
        sb.append(to64((b2 shl 16) or (b8 shl 8) or b14, 4))
        sb.append(to64((b3 shl 16) or (b9 shl 8) or b15, 4))
        sb.append(to64((b4 shl 16) or (b10 shl 8) or b5, 4))
        sb.append(to64(b11, 2))

        return sb.toString()
    }
}
