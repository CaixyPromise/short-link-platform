package com.caixy.shortlink.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

/**
 * Aes加解密操作类，支持多种模式。
 * <p>
 * 提供了两种加密结果的返回方式：
 * 1. 通过 CipherResult 类分别返回密文和 IV。
 * 2. 通过 combineIvAndCipherText 方法将 IV 和密文拼接并 Base64 编码为一个字符串。
 *
 * @author
 * @since 2025-01-14
 */
public class AesUtils
{

    /**
     * 默认的 Base64 编码密钥
     */
    private static final String DEFAULT_KEY = "S6osrowRYFmnY5ctNIbCua5kY1p1FrBf7kV9P3unJHU=";

    /**
     * 默认的加密模式
     */
    @Getter
    private static final EncryptionMode DEFAULT_MODE = EncryptionMode.GCM;

    /**
     * 用户如果想查看加解密后的结构信息，可使用此对象包装结果
     */
    @AllArgsConstructor
    @Getter
    public static class CipherResult
    {
        /**
        * 加密后的密文
        */
        private byte[] cipherText;
        /**
        * 如果某些模式需要 IV，则会存储到这里，否则为 null
        */
        private byte[] iv;
        public String getBase64CipherText()
        {
            // 如果cipherText不为空，则将cipherText转换为Base64编码的字符串
            return cipherText != null ? Base64.getEncoder().encodeToString(cipherText) : null;
        }

        public String getBase64Iv()
        {
            return iv != null ? Base64.getEncoder().encodeToString(iv) : null;
        }

        /**
         * 将 IV 和密文的字节数组拼接在一起，并进行 Base64 编码。
         *
         * @return 拼接后的 Base64 编码字符串
         */
        public String combineIvAndCipherText()
        {
            if (getCipherText() == null || getCipherText().length == 0)
            {
                throw new IllegalArgumentException("cipherResult 或 cipherText 不能为空");
            }
            byte[] iv = getIv();
            byte[] cipherText = getCipherText();

            if (iv != null && iv.length > 0)
            {
                byte[] combined = new byte[iv.length + cipherText.length];
                System.arraycopy(iv, 0, combined, 0, iv.length);
                System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
                return Base64.getEncoder().encodeToString(combined);
            }
            else
            {
                // 如果没有 IV，只返回密文的 Base64
                return Base64.getEncoder().encodeToString(cipherText);
            }
        }

        /**
         * 从拼接的 Base64 编码字符串中拆分出 IV 和密文的字节数组。
         *
         * @param combinedBase64 拼接后的 Base64 编码字符串
         * @param mode           加密模式，用于确定 IV 的长度
         * @return 拆分后的 CipherResult
         */
        public static CipherResult splitIvAndCipherText(String combinedBase64, EncryptionMode mode)
        {
            if (StringUtils.isBlank(combinedBase64))
            {
                throw new IllegalArgumentException("combinedBase64 不能为空");
            }
            byte[] combined = Base64.getDecoder().decode(combinedBase64);
            if (mode.getIvSize() > 0)
            {
                if (combined.length < mode.getIvSize())
                {
                    throw new IllegalArgumentException("combinedBase64 的长度小于 IV 的长度");
                }
                byte[] iv = new byte[mode.getIvSize()];
                byte[] cipherText = new byte[combined.length - mode.getIvSize()];
                System.arraycopy(combined, 0, iv, 0, mode.getIvSize());
                System.arraycopy(combined, mode.getIvSize(), cipherText, 0, cipherText.length);
                return new CipherResult(cipherText, iv);
            }
            else
            {
                // 如果不需要 IV，整个数据就是密文
                return new CipherResult(combined, null);
            }
        }
        public static CipherResult splitIvAndCipherText(String combinedBase64) {
            return splitIvAndCipherText(combinedBase64, DEFAULT_MODE);
        }

        @Override
        public String toString()
        {
            return "CipherResult{" +
                   "cipherText=" + Base64.getEncoder().encodeToString(cipherText) +
                   ", iv=" + (iv != null ? Base64.getEncoder().encodeToString(iv) : "null") +
                   '}';
        }
    }

    /**
     * 默认加密方法：使用默认模式 + 默认 key。<br>
     * - 如果模式需要IV，将自动生成并放入 CipherResult 返回
     *
     * @param content 明文
     * @return 加密结果 CipherResult（包含密文和 iv【如果需要】）
     */
    public static CipherResult encrypt(String content)
    {
        return encrypt(content, DEFAULT_KEY, null, DEFAULT_MODE);
    }

    /**
     * 默认解密方法：使用默认模式 + 默认 key。
     * <br> 需要外部传入 CipherResult，其中包含 cipherText（必需） 和 iv（如果需要）。
     *
     * @param cipherResult 加密时得到的加密结果，其中包含 cipherText 以及可能需要的 iv
     * @return 解密后的明文
     */
    public static String decrypt(CipherResult cipherResult)
    {
        return decrypt(cipherResult, DEFAULT_KEY, DEFAULT_MODE);
    }

    /**
     * 自定义加密（可切换模式、可自己提供 key 和 iv），iv 允许为空<br>
     * - 如果使用的加密模式需要 IV 而 iv 为空，则会随机生成<br>
     * - 如果使用的加密模式不需要 IV，则 iv 参数忽略
     *
     * @param content   要加密的明文
     * @param base64Key base64 编码的 AES key
     * @param base64Iv  base64 编码的 IV（可选），如果为空且模式需要，会自动生成
     * @param mode      枚举模式
     * @return 返回 CipherResult（包含密文和 iv【如果需要】）
     */
    public static CipherResult encrypt(String content, String base64Key, String base64Iv, EncryptionMode mode)
    {
        if (StringUtils.isBlank(content))
        {
            // 空字符串直接返回
            return new CipherResult(new byte[0], base64Iv != null ? Base64.getDecoder().decode(base64Iv) : null);
        }
        try
        {
            // 如果模式需要 IV 而外部又没传，就自动生成
            byte[] ivBytes = null;
            if (mode.getIvSize() > 0)
            {
                if (StringUtils.isBlank(base64Iv))
                {
                    ivBytes = new byte[mode.getIvSize()];
                    new SecureRandom().nextBytes(ivBytes);
                }
                else
                {
                    ivBytes = Base64.getDecoder().decode(base64Iv);
                }
            }

            // 初始化 Cipher
            Cipher cipher = mode.getCipher(base64Key,
                    ivBytes != null ? Base64.getEncoder().encodeToString(ivBytes) : null, Cipher.ENCRYPT_MODE);

            // 加密
            byte[] encrypted = cipher.doFinal(content.getBytes());

            return new CipherResult(encrypted, ivBytes);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * 自定义解密
     *
     * @param cipherResult cipherText + iv（可选）
     * @param base64Key    base64 编码的 AES key
     * @param mode         枚举模式
     * @return 解密后的明文
     */
    public static String decrypt(CipherResult cipherResult, String base64Key, EncryptionMode mode)
    {
        if (cipherResult == null
            || cipherResult.getCipherText() == null
            || cipherResult.getCipherText().length == 0)
        {
            return "";
        }
        try
        {
            byte[] decodedCipherText = cipherResult.getCipherText();

            byte[] ivBytes = cipherResult.getIv();
            // 若模式需要 iv，但没传 iv，则无法解密
            if (mode.getIvSize() > 0 && (ivBytes == null || ivBytes.length != mode.getIvSize()))
            {
                throw new IllegalArgumentException("IV is required for mode: " + mode.name());
            }

            String base64Iv = ivBytes != null ? Base64.getEncoder().encodeToString(ivBytes) : null;

            Cipher cipher = mode.getCipher(base64Key, base64Iv, Cipher.DECRYPT_MODE);
            byte[] decrypted = cipher.doFinal(decodedCipherText);
            return new String(decrypted);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * 使用指定模式对明文进行加密，自动使用默认 KEY 和随机 IV（若模式需要）。
     *
     * @param content 要加密的明文
     * @param mode    指定的加密模式（如 EncryptionMode.CBC / GCM / ...）
     * @return 加密后，将 (IV + 密文) 拼接并 Base64 得到的字符串
     */
    public static String encryptWithMode(String content, EncryptionMode mode) {
        // 利用已有方法：不传 iv，内部会自动随机生成（如果需要）
        CipherResult result = encrypt(content, DEFAULT_KEY, null, mode);
        // 拼接 iv + 密文后，返回一个 Base64 字符串
        return result.combineIvAndCipherText();
    }

    /**
     * 使用指定模式对 Base64(IV+密文) 进行解密，自动使用默认 KEY。
     *
     * @param combinedBase64 encryptWithMode(...) 返回的字符串
     * @param mode           加密模式（须与加密时一致）
     * @return 解密后的明文
     */
    public static String decryptWithMode(String combinedBase64, EncryptionMode mode) {
        // 先拆分出 iv 和密文
        CipherResult result = CipherResult.splitIvAndCipherText(combinedBase64, mode);
        // 使用默认 KEY + 指定模式进行解密
        return decrypt(result, DEFAULT_KEY, mode);
    }


    /**
     * 根据指定的加密模式生成密钥和可能的初始化向量（IV）。
     *
     * @param mode 指定的加密模式
     * @return 包含生成的密钥和IV的HashMap，键为 "key" 和 "iv"
     * @throws Exception 如果生成过程中出现错误
     */
    public static HashMap<String, String> generate(EncryptionMode mode) throws Exception
    {
        HashMap<String, String> result = new HashMap<>();
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(mode.getKeySize());
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] keyBytes = secretKey.getEncoded();
        result.put("key", Base64.getEncoder().encodeToString(keyBytes));

        if (mode.getIvSize() > 0)
        {
            byte[] ivs = new byte[mode.getIvSize()];
            new SecureRandom().nextBytes(ivs);
            result.put("iv", Base64.getEncoder().encodeToString(ivs));
        }
        return result;
    }


    /**
     * AES加密模式的枚举
     */
    @Getter
    public enum EncryptionMode
    {
        CBC("AES/CBC/PKCS5Padding", 256, 16),
        GCM("AES/GCM/NoPadding", 256, 12),
        ECB("AES/ECB/PKCS5Padding", 256, 0),
        CFB("AES/CFB/NoPadding", 256, 16),
        OFB("AES/OFB/NoPadding", 256, 16),
        CTR("AES/CTR/NoPadding", 256, 16);

        private final String cipherTransformation;
        private final int keySize;
        private final int ivSize;

        EncryptionMode(String cipherTransformation, int keySize, int ivSize)
        {
            this.cipherTransformation = cipherTransformation;
            this.keySize = keySize;
            this.ivSize = ivSize;
        }

        /**
         * 根据给定的加密模式、密钥、IV和操作模式创建Cipher对象。
         *
         * @param base64Key 密钥的Base64编码字符串
         * @param base64Iv  IV的Base64编码字符串，对于不需要IV的模式可以为null
         * @param opMode    操作模式，Cipher.ENCRYPT_MODE或Cipher.DECRYPT_MODE
         * @return 配置好的Cipher对象
         */
        public Cipher getCipher(String base64Key, String base64Iv, int opMode) throws Exception
        {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance(getCipherTransformation());
            if (this.getIvSize() > 0)
            {
                if (StringUtils.isBlank(base64Iv))
                {
                    throw new IllegalArgumentException("IV is required for mode: " + this.name());
                }
                byte[] ivBytes = Base64.getDecoder().decode(base64Iv);
                if (this == GCM)
                {
                    // GCMParameterSpec的第一个参数是认证标签的位长度，通常设置为128位
                    GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivBytes);
                    cipher.init(opMode, keySpec, gcmSpec);
                }
                else
                {
                    IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
                    cipher.init(opMode, keySpec, ivSpec);
                }
            }
            else
            {
                cipher.init(opMode, keySpec);
            }
            return cipher;
        }
    }

    public static void main(String[] args) throws Exception
    {
        String testString = "Hello, Encryption World!";
        System.out.println("原文: " + testString);

        // 1. 测试默认加密解密（默认 mode: GCM、默认 key）
        System.out.println("\n---[测试默认加解密]---");
        CipherResult encrypted = AesUtils.encrypt(testString);
        System.out.println("加密后 CipherResult: " + encrypted);
        String decrypted = AesUtils.decrypt(encrypted);
        System.out.println("解密后: " + decrypted);

        // 2. 测试默认加密后拼接并 Base64
        System.out.println("\n---[测试默认加密后拼接并 Base64]---");
        String combinedBase64 = encrypted.combineIvAndCipherText();
        System.out.println("拼接并 Base64 后: " + combinedBase64);

        // 3. 从拼接的 Base64 解密
        System.out.println("\n---[从拼接的 Base64 解密]---");
        CipherResult splitResult = CipherResult.splitIvAndCipherText(combinedBase64, DEFAULT_MODE);
        String decryptedFromCombined = decrypt(splitResult);
        System.out.println("解密后: " + decryptedFromCombined);

        // 4. 测试所有模式
        for (EncryptionMode mode : EncryptionMode.values())
        {
            System.out.println("\n---[测试模式: " + mode.name() + "]---");
            // 生成 key & iv
            HashMap<String, String> keys = generate(mode);
            String key = keys.get("key");
            String iv = keys.get("iv"); // 可能为 null

            // 加密
            CipherResult cipherResult = encrypt(testString, key, iv, mode);
            System.out.println("加密结果 CipherResult: " + cipherResult);

            // 拼接并 Base64
            String combined = cipherResult.combineIvAndCipherText();
            System.out.println("拼接并 Base64 后: " + combined);

            // 解密
            CipherResult split = CipherResult.splitIvAndCipherText(combined, mode);
            String plain = decrypt(split, key, mode);
            System.out.println("解密结果: " + plain);
        }
    }
}
