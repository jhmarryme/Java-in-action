package org.example.authorizationserver.jwk;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author clearmind
 */
public class RsaHelper {

    public static RSAKey generateRsa() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // @formatter:off
       return new RSAKey.Builder(getPublicKey())
               .privateKey(getPrivateKey())
               .build();
       // @formatter:on
    }

    public static PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource("private_key.pem");
        byte[] privateKeyBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String key = new String(privateKeyBytes, StandardCharsets.UTF_8);
        String privateStr = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] privateKeyDecodedBytes = Base64.getDecoder().decode(privateStr);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyDecodedBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static RSAPublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource("public_key.pem");
        byte[] publicKeyBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String key = new String(publicKeyBytes, StandardCharsets.UTF_8);
        String publicStr = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] publicKeyBase64Bytes = Base64.getDecoder().decode(publicStr);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBase64Bytes);
        PublicKey rsaPublicKey = keyFactory.generatePublic(publicKeySpec);
        return (RSAPublicKey) rsaPublicKey;
    }
}
