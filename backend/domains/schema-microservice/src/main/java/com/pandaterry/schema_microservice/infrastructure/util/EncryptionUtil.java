package com.pandaterry.schema_microservice.infrastructure.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptionUtil {
    @Value("${encryption.algorithm}")
    private String ALGORITHM;

    @Value("${encryption.key}")
    private String KEY; // 실제 운영에서는 환경 변수나 설정 파일에서 가져와야 함

    public String encrypt(String value) {
        try {
            if(value == null || value.isEmpty()) throw new Exception();
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new SchemaException(ErrorCode.ENCRYPTION_ERROR);
        }
    }

    public String decrypt(String encrypted) {
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new SchemaException(ErrorCode.DECRYPTION_ERROR);      
        }
    }
}