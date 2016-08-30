package tools;

/**
 * Created by arcearta on 2016/08/30.
 */
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by carlviar on 2016/08/29.
 */
public class PokeCipher {
    private final static String algorithm = "PBKDF2WithHmacSHA1";

    private final static String HEX = "0123456789ABCDEF";

    private static final String CP_ALGORITH = "AES";
    private static final String CP_KEY = "bjNidLfNhd0CZj8Mc49uTa2MrYEkjMENzNU09PHjuoZ9onfJccKGUWfY7pwdnRhdO9EWEydl17weTzOI";

    public static String encrypt(String data) throws NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
        KeySpec spec = new PBEKeySpec(CP_KEY.toCharArray(), CP_KEY.getBytes(), 128, 256);
        SecretKey tmp = skf.generateSecret(spec);
        SecretKey key = new SecretKeySpec(tmp.getEncoded(), CP_ALGORITH);
        Cipher cipher = Cipher.getInstance(CP_ALGORITH);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return toHex(cipher.doFinal(data.getBytes()));
    }

    private static byte[] toByte(String data) throws NullPointerException{
        int len = data.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(data.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    private static String toHex(byte[] doFinal) {
        StringBuffer result = new StringBuffer(2*doFinal.length);
        for (int i = 0; i < doFinal.length; i++) {
            result.append(HEX.charAt((doFinal[i]>>4)&0x0f)).append(HEX.charAt(doFinal[i]&0x0f));
        }
        return result.toString();
    }
}
