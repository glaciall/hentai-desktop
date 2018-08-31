package cn.org.hentai.desktop.util;

import java.security.MessageDigest;

public class MD5
{
    public static void main(String [] args){
        System.out.println(MD5.encode("web:::127.0.0.1:::29:::;S,I(\\\"**%k#/:!7D").length());
    }
    public static final String encode(String s)
    {
    	try
    	{
    		return MD5(s.getBytes("UTF-8"));
    	}
    	catch(Exception ex)
    	{
    		return null;
    	}
    }
    
    public static final String encode(byte[] buf)
    {
    	return MD5(buf);
    }
    
    private final static String MD5(byte[] btInput)
    {
    	char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        try
        {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++)
            {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            return null;
        }
    }

}
