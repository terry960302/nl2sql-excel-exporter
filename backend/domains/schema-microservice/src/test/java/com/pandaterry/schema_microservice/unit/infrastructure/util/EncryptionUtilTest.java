package com.pandaterry.schema_microservice.unit.infrastructure.util;

import com.pandaterry.schema_microservice.shared.exception.ErrorCode;
import com.pandaterry.schema_microservice.shared.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class EncryptionUtilTest {

    private EncryptionUtil encryptionUtil;
    private static final String TEST_ALGORITHM = "AES";
    private static final String TEST_KEY = "12345678901234567890123456789012";

    @BeforeEach
    void setUp() {
        encryptionUtil = new EncryptionUtil();
        ReflectionTestUtils.setField(encryptionUtil, "ALGORITHM", TEST_ALGORITHM);
        ReflectionTestUtils.setField(encryptionUtil, "KEY", TEST_KEY);
    }

    @Test
    void encrypt_성공() {
        // given
        String plainText = "test_password";

        // when
        String encrypted = encryptionUtil.encrypt(plainText);

        // then
        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEqualTo(plainText);
        assertThat(encrypted).isNotBlank();
        assertThat(encrypted).matches("^[A-Za-z0-9+/=]+$"); // Base64 형식 검증
    }

    @Test
    void encrypt_실패_빈문자열() {
        // given
        String plainText = "";

        // when & then
        assertThatThrownBy(() -> encryptionUtil.encrypt(plainText))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENCRYPTION_ERROR);
    }

    @Test
    void encrypt_실패_null() {
        // when & then
        assertThatThrownBy(() -> encryptionUtil.encrypt(null))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENCRYPTION_ERROR);
    }

    @Test
    void encrypt_decrypt_성공() {
        // given
        String plainText = "test_password";

        // when
        String encrypted = encryptionUtil.encrypt(plainText);
        String decrypted = encryptionUtil.decrypt(encrypted);

        // then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    void decrypt_실패_잘못된_형식() {
        // given
        String invalidEncrypted = "invalid_base64_format!@#";

        // when & then
        assertThatThrownBy(() -> encryptionUtil.decrypt(invalidEncrypted))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DECRYPTION_ERROR);
    }

    @Test
    void encrypt_decrypt_특수문자() {
        // given
        String plainText = "test@password#123!";

        // when
        String encrypted = encryptionUtil.encrypt(plainText);
        String decrypted = encryptionUtil.decrypt(encrypted);

        // then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    void encrypt_decrypt_한글() {
        // given
        String plainText = "테스트비밀번호123";

        // when
        String encrypted = encryptionUtil.encrypt(plainText);
        String decrypted = encryptionUtil.decrypt(encrypted);

        // then
        assertThat(decrypted).isEqualTo(plainText);
    }
}