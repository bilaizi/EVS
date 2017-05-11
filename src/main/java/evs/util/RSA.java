package evs.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Created by bilaizi on 17-3-9.
 */
public class RSA {
    public static void generateKey(String file1,String file2) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024,new SecureRandom());
            KeyPair kp = kpg.genKeyPair();
            PublicKey pbkey = kp.getPublic();
            PrivateKey prkey = kp.getPrivate();
            // 保存公钥
            FileOutputStream f1 = new FileOutputStream(file1);
            ObjectOutputStream b1 = new ObjectOutputStream(f1);
            b1.writeObject(pbkey);
            // 保存私钥
            FileOutputStream f2 = new FileOutputStream(file2);
            ObjectOutputStream b2 = new ObjectOutputStream(f2);
            b2.writeObject(prkey);
        } catch (Exception ignored) {
        }
    }

    public static PublicKey getPublicKey(String path) throws Exception {
        FileInputStream f = new FileInputStream(path);
        ObjectInputStream b = new ObjectInputStream(f);
         RSAPublicKey rsaPublicKey=(RSAPublicKey)b.readObject();
         b.close();
         return rsaPublicKey;
    }

    public static PrivateKey getPrivateKey(String path) throws Exception {
        // 读取私钥
        FileInputStream f = new FileInputStream(path);
        ObjectInputStream b = new ObjectInputStream(f);
        RSAPrivateKey rsaPrivateKey= (RSAPrivateKey) b.readObject();
        b.close();
        return rsaPrivateKey;
    }


    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        // 参数e,n
        RSAPublicKey pbk = (RSAPublicKey) publicKey;
        BigInteger e = pbk.getPublicExponent();
        BigInteger n = pbk.getModulus();
        //System.out.println("e= " + e);
        //System.out.println("n= " + n);
        // 获取明文m
        byte ptext[] = plainText.getBytes("UTF-8");
        BigInteger m = new BigInteger(ptext);
        // 计算密文c
        BigInteger c = m.modPow(e, n);
        //System.out.println("c= " + c);
        // 保存密文
        return c.toString();
    }

    public static String decrypt(String ciphertext, PrivateKey privateKey) throws Exception {
        BigInteger c = new BigInteger(ciphertext);
        RSAPrivateKey prk = (RSAPrivateKey) privateKey;
        BigInteger d = prk.getPrivateExponent();
        // 获取私钥参数及解密
        BigInteger n = prk.getModulus();
        //System.out.println("d= " + d);
        //System.out.println("n= " + n);
        BigInteger m = c.modPow(d, n);
        // 显示解密结果
        //System.out.println("m= " + m);
        byte[] mt = m.toByteArray();
        return new String(mt, "utf-8");
    }

    public static void main(String args[]) {
        try {
            for(int i=1;i<=101;i++)
            generateKey("publickey"+i+".dat","privatekey"+i+".dat");
            //String s = "How are you ! 你好！";
            //String ciphertext = encrypt(s, getPublicKey("publickey.dat"));
            //System.out.println(ciphertext);
            //String plaintext = (String) decrypt(ciphertext, getPrivateKey("privatekey.dat"));
            //System.out.println(plaintext);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
