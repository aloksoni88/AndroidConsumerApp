package com.clicbrics.consumer.utils;

/**
 * Created by Ranjeet Jha on 14-12-2015.
 */

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *  * A utility class that encrypts or decrypts a file.
 *  * @author www.codejava.net
 *  *
 *  
 */
public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static InputStream decrypt(String key, InputStream inputStream) throws Exception {
        InputStream is;
        try {
            Key secretKey = new SecretKeySpec(generateKey(key), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] inputBytes = new byte[ inputStream.available()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes,0,inputBytes.length);
            Log.i("CryptUtils", "decrypt: " + outputBytes.toString());
            //byte[] outputBytes = cipher.doFinal(Base64.decode(inputBytes,Base64.DEFAULT));

            is = new ByteArrayInputStream(outputBytes);
            inputStream.close();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex) {
            ex.printStackTrace();
            throw new Exception("Error/ exception in do encrypt", ex);
        }
        return is;
    }

    /*public static InputStream decryptKey(String key, InputStream inputStream) throws Exception {
        try {
            Cipher c = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[16];
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            c.init(Cipher.DECRYPT_MODE, key, paramSpec);
            out = new CipherOutputStream(out, c);
            int count = 0;
            byte[] buffer = new byte[DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE];
            while ((count = in.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
            }
        } finally {
            out.close();
        }
    }*/


    public static byte[] generateKey(String password) throws Exception {
        byte[] keyStart = password.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
        //SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
        //SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        //SecureRandom sr = new SecureRandom();
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        Log.i("CryptUtils", "generateKey: " + skey.getEncoded().toString());
        return skey.getEncoded();
    }

    static String getToken() {
        return "VAEgTyl5Eyw";
    }

    static String getKey() {
        return "78jh732HJY";
    }



    public static String decrypt(byte[] encryptedIvTextBytes, String key) throws Exception {
        int ivSize = 16;
        int keySize = 16;

        // Extract IV.
        byte[] iv = new byte[ivSize];
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted part.
        int encryptedSize = encryptedIvTextBytes.length - ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

        // Hash key.
        byte[] keyBytes = new byte[keySize];
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(key.getBytes());
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Decrypt.
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

        return new String(decrypted);
    }



}