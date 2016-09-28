package com.example.user.myapplication;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by user on 2016/9/14.
 */
public class AESJni {
    private static final String TAG ="aesjni" ;

    public native byte[] getKeyValue();

    public native byte[] getIv();

    private  byte[] keyValue;
    private  byte[] iv;

    private  SecretKey key;
    private  AlgorithmParameterSpec paramSpec;
    private  Cipher ecipher;

    static {
        System.loadLibrary("AESJni");

    }

    public AESJni() {
        keyValue = getKeyValue();
        iv = getIv();
//        Log.e(TAG, "keyValue:"+ new String(keyValue)+"---iv:"+new String(iv));
        if (null != keyValue && null != iv) {
            KeyGenerator kgen;
            try {
                kgen = KeyGenerator.getInstance("AES");
//                SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "Crypto");
//                random.setSeed(keyValue);
                kgen.init(128, new SecureRandom(keyValue));
                key = kgen.generateKey();
                paramSpec = new IvParameterSpec(iv);
                ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加密
     *
     * @param msg
     * @return
     */
    public String encode(String msg) {
        String strHex = "";
        try {
            //用秘钥和一组算法参数初始化 此cipher
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            strHex = byte2hex(ecipher.doFinal(msg.getBytes()));
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return strHex;
    }

    public String decode(String value) {
        String strContent = "";
        try {
            ecipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
          strContent = new String(ecipher.doFinal(hex2byte(value)));
            Log.e(TAG, "strContent: "+strContent );
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return strContent;
    }

    /**
     * 将二进制转为 16进制字符串
     *
     * @param bytes 二进制字节数组
     * @return String
     */
    private String byte2hex(byte[] bytes) {
        int length = bytes.length;
        String stmp = "";
        StringBuffer strbuf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            stmp = Integer.toHexString(bytes[i] & 0XFF);
            if (stmp.length() == 1)//小于10 前面补0
                strbuf.append("0");
            strbuf.append(stmp);
        }
        return strbuf.toString();
    }

    /**
     * 十六进制字符串 转 二进制数组
     *
     * @param hex
     * @return
     */
//    private byte[] hex2byte(String hex) {
//        byte[] ret = new byte[8];
//        byte[] tmp = hex.getBytes();
//        for (int i = 0; i < 8; i++) {
//            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
//        }
//        return ret;
//    }

    /**
     * 将两个ASCII 字符组合成一个字节 如 EF--> 0XFF
     *
     * @param b
     * @param b1
     * @return
     */
    private byte uniteBytes(byte b, byte b1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{b})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{b1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }
    /**
     * 十六进制字符串 转 二进制数组
     *
     * @param src
     * @return
     */
    private byte[] hex2byte(String src) {
        int length = src.length();
        if (length < 1) {
            return null;
        }
        byte[] encrypted = new byte[length / 2];
        for (int i = 0; i < length/2; i++) {
            int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);//取高位字节
            int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);//取低位字节
            encrypted[i] = (byte) (high * 16 + low);
        }
        return encrypted;
    }
}
