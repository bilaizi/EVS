package evs.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * Created by bilaizi on 17-3-20.
 */
public class AES {
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        kg.init(128,new SecureRandom());
        SecretKey secretKey = kg.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static String encrypt(String source, String key) throws Exception {
        byte[] sourceBytes = source.getBytes("UTF-8");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, KEY_ALGORITHM));
        byte[] decrypted = cipher.doFinal(sourceBytes);
        return Base64.getEncoder().encodeToString(decrypted);
    }

    public static String decrypt(String encryptStr, String key) throws Exception {
        byte[] sourceBytes = Base64.getDecoder().decode(encryptStr);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, KEY_ALGORITHM));
        byte[] decoded = cipher.doFinal(sourceBytes);
        return new String(decoded, "UTF-8");
    }


    public static void main(String[] args) throws Exception {
        String message = "MESSAGE 比";
        String password = generateKey();
        String encrypted = encrypt(message, password);
        String decrypted = decrypt(encrypted, password);
        System.out.println("Encrypt(\"" + message + "\", \"" + password + "\") = \"" + encrypted + "\"");
        System.out.println("Decrypt(\"" + encrypted + "\", \"" + password + "\") = \"" + decrypted + "\"");
    }
}
