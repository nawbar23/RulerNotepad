//package com.nawbar.rulernotepad.email;
//
//import android.content.Context;
//import android.security.KeyPairGeneratorSpec;
//import android.util.Base64;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.math.BigInteger;
//import java.security.KeyPairGenerator;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//import java.util.ArrayList;
//import java.util.Calendar;
//
//import javax.crypto.Cipher;
//import javax.crypto.CipherInputStream;
//import javax.crypto.CipherOutputStream;
//import javax.security.auth.x500.X500Principal;
//
///**
// * Created by Bartosz Nawrot on 2017-09-04.
// */
//
//public class PasswordManager {
//
//    private static final String keyStoreAlias = "key_alias";
//
//    private Context context;
//    private KeyStore keyStore;
//
//    public PasswordManager() {
//        try {
//            keyStore = KeyStore.getInstance("AndroidKeyStore");
//            keyStore.load(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String getPassword() throws Exception {
//
//        return "password";
//    }
//
//    public void setPassword(String password) throws Exception{
//
//    }
//
//    private void initializeKeyStore() {
//        try {
//            if (!keyStore.containsAlias(keyStoreAlias)) {
//                Calendar start = Calendar.getInstance();
//                Calendar end = Calendar.getInstance();
//                end.add(Calendar.YEAR, 1);
//                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
//                        .setAlias(keyStoreAlias)
//                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
//                        .setSerialNumber(BigInteger.ONE)
//                        .setStartDate(start.getTime())
//                        .setEndDate(end.getTime())
//                        .build();
//                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
//                generator.initialize(spec);
//                generator.generateKeyPair();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String encryptString(String decrypted) throws Exception {
//        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(keyStoreAlias, null);
//        RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
//
//        Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
//        input.init(Cipher.ENCRYPT_MODE, publicKey);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        CipherOutputStream cipherOutputStream = new CipherOutputStream(
//                outputStream, input);
//        cipherOutputStream.write(decrypted.getBytes("UTF-8"));
//        cipherOutputStream.close();
//
//        byte [] values = outputStream.toByteArray();
//        return Base64.encodeToString(values, Base64.DEFAULT);
//    }
//
//    public String decryptString(String encrypted) throws Exception {
//        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(keyStoreAlias, null);
//        RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
//
//        Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
//        output.init(Cipher.DECRYPT_MODE, privateKey);
//
//        CipherInputStream cipherInputStream = new CipherInputStream(
//                new ByteArrayInputStream(Base64.decode(encrypted, Base64.DEFAULT)), output);
//        ArrayList<Byte> values = new ArrayList<>();
//        int nextByte;
//        while ((nextByte = cipherInputStream.read()) != -1) {
//            values.add((byte)nextByte);
//        }
//
//        byte[] bytes = new byte[values.size()];
//        for(int i = 0; i < bytes.length; i++) {
//            bytes[i] = values.get(i);
//        }
//
//        return new String(bytes, 0, bytes.length, "UTF-8");
//    }
//
//    public class NoPasswordException extends Exception {
//    }
//}
