package maks.molch.dmitr.badminton_service.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Утилитарный класс для криптографических операций.
 */
public final class CryptoUtils {

    private CryptoUtils() {
    }

    /**
     * Вычисляет хеш SHA-256 для входных данных.
     *
     * @param data входные данные для хеширования
     * @return хеш SHA-256 в виде массива байт
     * @throws SecurityException если алгоритм SHA-256 недоступен
     */
    public static byte[] sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("Ошибка при вычислении SHA-256", e);
        }
    }

    /**
     * Вычисляет HMAC-SHA256 для данных с использованием указанного ключа.
     *
     * @param key  секретный ключ
     * @param data данные для аутентификации
     * @return HMAC-SHA256 в виде массива байт
     * @throws SecurityException если алгоритм HMAC-SHA256 недоступен или ключ недействителен
     */
    public static byte[] hmacSha256(byte[] key, byte[] data) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
            sha256Hmac.init(secretKey);
            return sha256Hmac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SecurityException("Ошибка при вычислении HMAC-SHA256", e);
        }
    }

    /**
     * Преобразует массив байт в шестнадцатеричную строку.
     *
     * @param bytes массив байт для преобразования
     * @return шестнадцатеричное строковое представление
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Loads a private key from a Base64-encoded string.
     *
     * @param base64 Base64-encoded private key (PKCS#8 format)
     * @return the loaded PrivateKey
     * @throws IllegalStateException if the key cannot be loaded
     */
    public static PrivateKey loadPrivateKey(String base64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64.replaceAll("\\s+", ""));
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Cannot load private key", e);
        }
    }

    /**
     * Loads a public key from a Base64-encoded string.
     *
     * @param base64 Base64-encoded public key (X.509 format)
     * @return the loaded RSAPublicKey
     * @throws IllegalStateException if the key cannot be loaded
     */
    public static RSAPublicKey loadPublicKey(String base64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64.replaceAll("\\s+", ""));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Cannot load public key", e);
        }
    }
}
