package tools;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class Crypter {
	
	
	private static final String ENCRYPTION_ALGORITHM = "AES";
	private static final String UTF_8_ENCODE = "UTF-8";
	private static final String SHA_256_ENCODE = "SHA-256";

	private static final String SECRET_KEY = DigestUtils.md5Hex(System.getProperty("TLK_SECRET", "tinnlabaskey"));
	private static final String TRANSFORMATION = System.getProperty("SSO_AES_TRANSFORMATION", "AES/CTR/NoPadding");

	//private static final String TRANSFORMATION = System.getProperty("SSO_AES_TRANSFORMATION", "AES/ECB/PKCS5Padding");
	
	
	private static final String SEPARATOR = "-";

	private Crypter(){
		
	}
	
	public static String encode(String base, String algorithm) {
        try{
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(base.getBytes(UTF_8_ENCODE));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                	hexString.append('0');
                }
                hexString.append(hex);
            }

	        return hexString.toString();
	    } catch(Exception ex){
	       throw new SecurityException(ex);
	    }
	}
	
	public static String sha256(String base) {
		return encode(base, SHA_256_ENCODE);       
	}
	
	public static String encryptAES(String value) {
		return encryptAES(value, SECRET_KEY);
	}

	public static String encryptAES(String value, String privateKey) {
		try {
			SecretKeySpec secret = secretKeyWithSha256(privateKey, ENCRYPTION_ALGORITHM);
			Cipher cipher = getCipherWithConfiguredProvider(TRANSFORMATION);
			initCipher(cipher, Cipher.ENCRYPT_MODE, secret);
			byte[] encryptedValue = cipher.doFinal(value.getBytes(UTF_8_ENCODE));
			byte[] iv = cipher.getIV();
			if (iv == null){
				return String.format("%s%s", "1", SEPARATOR, DatatypeConverter.printBase64Binary(encryptedValue));
			} else {
				byte[] paddedEncryptedValue = concat(encryptedValue, iv);
				return String.format("%s%s%s", "2", SEPARATOR, DatatypeConverter.printBase64Binary(paddedEncryptedValue));
			}
		} catch (UnsupportedEncodingException e) {
			throw new SecurityException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new SecurityException("No se encontr� el algoritmo de cifrado", e);
		} catch (NoSuchPaddingException e) {
			throw new SecurityException("No se encontr� padding", e);
		} catch (InvalidKeyException e) {
			throw new SecurityException("Llave no v�lida", e);
		} catch (IllegalBlockSizeException e) {
			throw new SecurityException("Tama�o de bloque no permitido", e);
		} catch (BadPaddingException e) {
			throw new SecurityException("Tama�o de padding no permitido", e);
		}
	}

	private static byte[] concat(byte[] a, byte[] b) {
		byte[] result = new byte[b.length + a.length];
		System.arraycopy(b, 0, result, 0, b.length);
		System.arraycopy(a, 0, result, b.length, a.length);
		return result;
	}
	
	public static String decryptAES(String value){
		return decryptAES(value, SECRET_KEY);
	}
	
	public static String decryptAES(String value, String privateKey){
	    int sepIndex = value.indexOf(SEPARATOR);
	    if (sepIndex < 0) {
	      return decryptAESVersion0(value, privateKey);
	    } else {
	      String version = value.substring(0, sepIndex);
	      String data = value.substring(sepIndex + 1, value.length());
	      if ("1".equals(version)){
	    	  return decryptAESVersion1(data, privateKey);
	      } else if ("2".equals(version)){
	    	  return decryptAESVersion2(data, privateKey);
	      } else {
	    	  throw new SecurityException("Unknown encryption version");
	      }
	    }
	}
	
	private static String decryptAESVersion2(String value, String privateKey) {
		try {
			byte[] data = DatatypeConverter.parseBase64Binary(value);				    		
			//byte[] data = value.getBytes(); //DatatypeConverter.parseBase64Binary(value);
			SecretKeySpec secret = secretKeyWithSha256(privateKey, ENCRYPTION_ALGORITHM);
			
			//System.out.println("secret: "+ secret.getAlgorithm() + "aaa:  " + secret.getFormat());
			Cipher cipher = getCipherWithConfiguredProvider(TRANSFORMATION);
			int blockSize = cipher.getBlockSize();
			//System.out.println("BlockSize"+blockSize );
			byte[] iv = Arrays.copyOfRange(data, 0, blockSize);			
			
			byte[] payload = Arrays.copyOfRange(data, blockSize, data.length);
			
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			
			byte[] finalbytes = cipher.doFinal(payload);
						
	        /*for(int i=0; i< finalbytes.length ; i++){
				
				System.out.println(finalbytes[i] & 0xFF);
				
			}*/			
			return new String(finalbytes, UTF_8_ENCODE);
		} catch (UnsupportedEncodingException e) {
			throw new SecurityException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new SecurityException("No se encontr� el algoritmo de cifrado", e);
		} catch (NoSuchPaddingException e) {
			throw new SecurityException("No se encontr� padding", e);
		} catch (InvalidKeyException e) {
			throw new SecurityException("Llave no v�lida", e);
		} catch (IllegalBlockSizeException e) {
			throw new SecurityException("Tama�o de bloque no permitido", e);
		} catch (BadPaddingException e) {
			throw new SecurityException("Tama�o de padding no permitido", e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new SecurityException("Tama�o de parametro no permitido", e);
		}
	}

	private static String decryptAESVersion1(String value, String privateKey) {
		try {
			byte[] data = DatatypeConverter.parseBase64Binary(value);
			SecretKeySpec secret = secretKeyWithSha256(privateKey, ENCRYPTION_ALGORITHM);
			Cipher cipher = getCipherWithConfiguredProvider(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secret);
			return new String(cipher.doFinal(data), UTF_8_ENCODE);
		} catch (UnsupportedEncodingException e) {
			throw new SecurityException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new SecurityException("No se encontr� el algoritmo de cifrado", e);
		} catch (NoSuchPaddingException e) {
			throw new SecurityException("No se encontr� padding", e);
		} catch (InvalidKeyException e) {
			throw new SecurityException("Llave no v�lida", e);
		} catch (IllegalBlockSizeException e) {
			throw new SecurityException("Tama�o de bloque no permitido", e);
		} catch (BadPaddingException e) {
			throw new SecurityException("Tama�o de padding no permitido", e);
		}
	}

	private static String decryptAESVersion0(String value, String privateKey) {
		try {
			byte[] raw = privateKey.substring(0, 16).getBytes(UTF_8_ENCODE);
			SecretKeySpec skeySpec = new SecretKeySpec(raw, ENCRYPTION_ALGORITHM);
			Cipher cipher = getCipherWithConfiguredProvider(ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return new String(cipher.doFinal(Hex.decodeHex(value.toCharArray())));
		} catch (UnsupportedEncodingException e) {
			throw new SecurityException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new SecurityException("No se encontr� el algoritmo de cifrado", e);
		} catch (NoSuchPaddingException e) {
			throw new SecurityException("No se encontr� padding", e);
		} catch (InvalidKeyException e) {
			throw new SecurityException("Llave no v�lida", e);
		} catch (IllegalBlockSizeException e) {
			throw new SecurityException("Tama�o de bloque no permitido", e);
		} catch (BadPaddingException e) {
			throw new SecurityException("Tama�o de padding no permitido", e);
		} catch (DecoderException e) {
			throw new SecurityException("Error decodificando hexagesimal", e);
		}
	}

	private static SecretKeySpec secretKeyWithSha256(String privateKey, String algorithm) throws UnsupportedEncodingException {
		byte[] raw = sha256(privateKey.toString()).substring(0, 16).getBytes(UTF_8_ENCODE);		
		/*for(int i=0; i< raw.length ; i++){		
			System.out.println(raw[i] & 0xFF);		
		}*/
		return new SecretKeySpec(raw, algorithm);
	}
	
	 private static Cipher getCipherWithConfiguredProvider(String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance(transformation);		
		//return Cipher.getInstance("AES/CBC/PKCS7Padding");
	 }
	 
	 private static void initCipher(Cipher cipher, int mode, SecretKeySpec key) throws InvalidKeyException{
		 cipher.init(mode, key);
	 }

	public static void main(String args[]){
		String value = "password";
		String encryptedValue = Crypter.encryptAES(value);

		String decryptedValue = Crypter.decryptAES(encryptedValue);

		System.out.println("Desencriptado: " + decryptedValue);

	}
	
}
