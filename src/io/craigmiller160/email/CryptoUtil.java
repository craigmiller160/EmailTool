package io.craigmiller160.email;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;

/**
 * Created by craig on 3/12/17.
 */
public class CryptoUtil {

    private static final String STORE_TYPE = "JCEKS";
    private static final String ALIAS = "savekey";
    private static final String STORE_PASS_PROP = "storepass";
    private static final String STORE_PATH = "io/craigmiller160/email/keystore.jceks";
    private static final String PROPS_PATH = "io/craigmiller160/email/email.properties";
    private static final String ALGORITHM = "AES";

    private static final Object LOCK = new Object();
    private static CryptoUtil instance;

    private final SecretKey key;

    private CryptoUtil() throws Exception{
        KeyStore keyStore = null;
        try{
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream(PROPS_PATH));
            InputStream keystoreStream = getClass().getClassLoader().getResourceAsStream(STORE_PATH);
            keyStore = KeyStore.getInstance(STORE_TYPE);
            keyStore.load(keystoreStream, properties.getProperty(STORE_PASS_PROP).toCharArray());
            this.key = (SecretKey) keyStore.getKey(ALIAS, properties.getProperty(STORE_PASS_PROP).toCharArray());
        }
        catch(KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException ex){
            throw new Exception("Unable to load KeyStore", ex);
        }
    }

    public static CryptoUtil getInstance() throws Exception{
        if(instance == null){
            synchronized (LOCK){
                if(instance == null){
                    instance = new CryptoUtil();
                }
            }
        }
        return instance;
    }

    public byte[] encryptData(byte[] data) throws Exception{
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        return cipher.doFinal(data);
    }

    public byte[] decryptData(byte[] data) throws Exception{
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        return cipher.doFinal(data);
    }

}
