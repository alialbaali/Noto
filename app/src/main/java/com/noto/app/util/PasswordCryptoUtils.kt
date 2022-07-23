package com.noto.app.util

import com.noto.app.domain.model.PasswordData
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.security.SecureRandom

private const val SaltSize = 32
private const val HashSize = 64
private const val Parallelism = 1
private const val MemoryInKB = 16_384 // 16 MB
private const val Iterations = 10
private const val Version = Argon2Parameters.ARGON2_VERSION_13
private const val Type = Argon2Parameters.ARGON2_id

object PasswordCryptoUtils {

    private val secureRandom = SecureRandom()

    private val generator = Argon2BytesGenerator()

    fun hashPassword(password: String): PasswordData {
        val hash = ByteArray(HashSize)
        val salt = generateSalt()
        val parameters = buildParameters(salt)
        generator.init(parameters)
        generator.generateBytes(password.toByteArray(), hash)
        val encodedHashedPassword = hash.encodeToString()
        val encodedParameters = parameters.encodeToString()
        return PasswordData(encodedHashedPassword, encodedParameters)
    }

    fun hashPassword(password: String, encodedParameters: String): PasswordData {
        val hash = ByteArray(HashSize)
        val parameters = encodedParameters.decodeToParameters()
        generator.init(parameters)
        generator.generateBytes(password.toByteArray(), hash)
        val encodedHashedPassword = hash.encodeToString()
        return PasswordData(encodedHashedPassword, encodedParameters)
    }

    private fun shouldRehashAgain(parameters: Argon2Parameters): Boolean {
        // Check salt size or not?
        return parameters.salt.size < SaltSize || parameters.memory < MemoryInKB || parameters.iterations < Iterations
    }

    private fun buildParameters(salt: ByteArray) = Argon2Parameters.Builder(Type)
        .withSalt(salt)
        .withParallelism(Parallelism)
        .withMemoryAsKB(MemoryInKB)
        .withIterations(Iterations)
        .withVersion(Version)
        .build()

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(SaltSize)
        secureRandom.nextBytes(salt)
        return salt
    }

    private fun Argon2Parameters.encodeToString() = buildString {
        append("$")
        when (type) {
            Argon2Parameters.ARGON2_d -> append("argon2d")
            Argon2Parameters.ARGON2_i -> append("argon2i")
            Argon2Parameters.ARGON2_id -> append("argon2id")
            else -> unknownAlgorithm()
        }
        append("\$v=")
        append(version)
        append("\$m=")
        append(memory)
        append(",t=")
        append(iterations)
        append(",p=")
        append(lanes)
        append("$")
        append(salt.encodeToString())
    }

    private fun String.decodeToParameters(): Argon2Parameters {
        val parts = split('$').filterNot { it.isBlank() }
        val builder = when (parts[0]) {
            "argon2d" -> Argon2Parameters.Builder(Argon2Parameters.ARGON2_d)
            "argon2i" -> Argon2Parameters.Builder(Argon2Parameters.ARGON2_i)
            "argon2id" -> Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            else -> unknownAlgorithm()
        }
        val salt = parts.last().decodeToByteArray()
        builder.withSalt(salt)
        val version = parts.getIntValueAtIndex(1)
        builder.withVersion(version)
        val options = parts[2].split(',')
        val memory = options.getIntValueAtIndex(0)
        builder.withMemoryAsKB(memory)
        val iterations = options.getIntValueAtIndex(1)
        builder.withIterations(iterations)
        val parallelism = options.getIntValueAtIndex(2)
        builder.withParallelism(parallelism)
        return builder.build()
    }

    private fun List<String>.getIntValueAtIndex(index: Int) = this[index].split('=').last().toInt()

    private fun unknownAlgorithm(): Nothing = error("Unknown Algorithm.")
}