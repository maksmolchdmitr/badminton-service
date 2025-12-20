package maks.molch.dmitr.badminton_service.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}
