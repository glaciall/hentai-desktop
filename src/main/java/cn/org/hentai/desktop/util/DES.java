package cn.org.hentai.desktop.util;

/**
 * Created by Expect on 2018/1/25.
 */

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

public class DES
{
    public static void main(String[] args) throws Exception
    {
        String data = "ve9wyarrojvhaiorgiaohjkfda";
        String password = "12345678";
        byte[] des = encrypt(data.getBytes(), password);
        System.out.println("Original: " + ByteUtils.toString(data.getBytes()));
        System.out.println("Encrypt:  " + ByteUtils.toString(des));
        des = decrypt(des, "22345678");
        System.out.println("Decrypt:  " + ByteUtils.toString(des));
    }

    public static byte[] encrypt(byte[] data, String key) throws Exception
    {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception
    {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }
}