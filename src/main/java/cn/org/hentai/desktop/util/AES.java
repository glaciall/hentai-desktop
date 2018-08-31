package cn.org.hentai.desktop.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AES
{
	public static final byte[] encode(byte[] buf, String key)
	{
		try
		{
			KeyGenerator kGen = KeyGenerator.getInstance("AES");
			SecureRandom sRandom = SecureRandom.getInstance("SHA1PRNG");
			sRandom.setSeed(key.getBytes("UTF-8"));
			kGen.init(128, sRandom);
			SecretKey secretKey = kGen.generateKey();
			byte[] encodeFormat = secretKey.getEncoded();
			SecretKeySpec keySpec = new SecretKeySpec(encodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			return cipher.doFinal(buf);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	public static final byte[] decode(byte[] buf, String key)
	{
		try
		{
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sRandom = SecureRandom.getInstance("SHA1PRNG");
			sRandom.setSeed(key.getBytes("UTF-8"));
	        kgen.init(128, sRandom);
	        SecretKey secretKey = kgen.generateKey();
	        byte[] enCodeFormat = secretKey.getEncoded();
	        SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.DECRYPT_MODE, keySpec);
	       	byte[] result = cipher.doFinal(buf);
	       	return result;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception
	{
		byte[] data = "abcdefgh".getBytes();
		System.out.println(ByteUtils.toString(data = encode(data, "ABCDEFG")));
		System.out.println(new String(decode(data, "ABCDEFG")));
	}
}
