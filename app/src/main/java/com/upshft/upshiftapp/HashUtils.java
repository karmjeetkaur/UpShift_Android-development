package com.upshft.upshiftapp;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Created by Andrew on 4/23/2016.
 */
public class HashUtils {
    private static byte[] getSHA256Bytes(String value) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            return digest.digest(value.getBytes());
        } catch (NoSuchAlgorithmException e1) {
            return null;
        }
    }

    public static String getSHA256(String value) {
        byte[] data = getSHA256Bytes(value);

        return (String.format("%0" + (data.length*2) + "X", new BigInteger(1, data))).toUpperCase();
    }
}
