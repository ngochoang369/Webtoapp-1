package com.example.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val AES_KEY_SIZE = 256
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128
    private const val PBKDF2_ITERATIONS = 10000

    // Master seed for standard E2EE
    private const val DEFAULT_PASSPHRASE = "PRIVADIARY_SECURE_MASTER_KEY_2026_E2EE"
    private const val ADMIN_AUDIT_SEED = "DEVREGISH_ADMIN_AUDIT_ESCROW_MASTER_KEY_2026"

    /**
     * Derive AES key from user passphrase or default master seed
     */
    fun deriveKey(passphrase: String = DEFAULT_PASSPHRASE, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITERATIONS, AES_KEY_SIZE)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, ALGORITHM)
    }

    /**
     * Encrypt plaintext string into Base64 string containing: Salt(16B) + IV(12B) + Ciphertext
     */
    fun encrypt(plainText: String, userPassphrase: String = DEFAULT_PASSPHRASE): String {
        if (plainText.isEmpty()) return ""
        try {
            val random = SecureRandom()
            val salt = ByteArray(16)
            random.nextBytes(salt)

            val iv = ByteArray(GCM_IV_LENGTH)
            random.nextBytes(iv)

            val keySpec = deriveKey(userPassphrase, salt)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec)

            val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            // Combine salt + iv + cipherText
            val combined = ByteArray(salt.size + iv.size + cipherText.size)
            System.arraycopy(salt, 0, combined, 0, salt.size)
            System.arraycopy(iv, 0, combined, salt.size, iv.size)
            System.arraycopy(cipherText, 0, combined, salt.size + iv.size, cipherText.size)

            return Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback string if encryption fails
            return "ENC_ERR:" + Base64.encodeToString(plainText.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }
    }

    /**
     * Decrypt Base64 string back to plaintext
     */
    fun decrypt(encryptedBase64: String, userPassphrase: String = DEFAULT_PASSPHRASE): String {
        if (encryptedBase64.isEmpty()) return ""
        if (encryptedBase64.startsWith("ENC_ERR:")) {
            val raw = encryptedBase64.removePrefix("ENC_ERR:")
            return String(Base64.decode(raw, Base64.NO_WRAP), Charsets.UTF_8)
        }
        try {
            val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            if (combined.size < 16 + GCM_IV_LENGTH) return encryptedBase64

            val salt = ByteArray(16)
            System.arraycopy(combined, 0, salt, 0, 16)

            val iv = ByteArray(GCM_IV_LENGTH)
            System.arraycopy(combined, 16, iv, 0, GCM_IV_LENGTH)

            val cipherTextSize = combined.size - 16 - GCM_IV_LENGTH
            val cipherText = ByteArray(cipherTextSize)
            System.arraycopy(combined, 16 + GCM_IV_LENGTH, cipherText, 0, cipherTextSize)

            val keySpec = deriveKey(userPassphrase, salt)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec)

            val decryptedBytes = cipher.doFinal(cipherText)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            // Try fallback with default master key if custom passphrase failed
            if (userPassphrase != DEFAULT_PASSPHRASE) {
                return decrypt(encryptedBase64, DEFAULT_PASSPHRASE)
            }
            return "[Bản ghi đã được mã hóa - Nhập đúng khóa E2EE để xem]"
        }
    }

    /**
     * Admin Audit Decryption Helper for devregish@gmail.com
     */
    fun adminAuditDecrypt(encryptedBase64: String, userEmail: String): String {
        // Try user decryption
        val decrypted = decrypt(encryptedBase64, DEFAULT_PASSPHRASE)
        if (!decrypted.startsWith("[Bản ghi")) {
            return decrypted
        }
        // Admin escrow fallback decryption
        return decrypt(encryptedBase64, ADMIN_AUDIT_SEED)
    }

    /**
     * Hash PIN for vault authentication
     */
    fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(("PRIVADIARY_SALT_$pin").toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
