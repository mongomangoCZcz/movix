package com.example.movix.data.repository

import com.example.movix.data.remote.WebshareApiService
import com.example.movix.domain.utils.Md5Crypt
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val webshareApi: WebshareApiService
) {
    private val REALM = ":Webshare:"

    private fun sha1(input: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun login(username: String, passwordRaw: String): Result<String> {
        return try {
            val saltResponse = webshareApi.getSalt(username)
            if (saltResponse.status != "OK" || saltResponse.salt == null) {
                return Result.failure(Exception(saltResponse.message ?: "Failed to get salt"))
            }

            val salt = saltResponse.salt
            val cryptedPass = Md5Crypt.md5Crypt(passwordRaw, salt)
            val encryptedPass = sha1(cryptedPass)
            val passDigest = md5(username + REALM + encryptedPass)

            val loginResponse = webshareApi.login(
                username = username,
                passwordHashed = encryptedPass,
                digest = passDigest
            )

            if (loginResponse.status == "OK" && loginResponse.token != null) {
                Result.success(loginResponse.token)
            } else {
                Result.failure(Exception(loginResponse.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
