package com.eina.chat.backendapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EncryptionAPITest {

    @Autowired
    EncryptionAPI encryptionAPI;

    @Test
    public void symmetricEncryptionMsg() {
        String msg = "testMsgSYM";
        String encrypted = encryptionAPI.symmetricEncryptString(msg);
        System.out.println("MSG encrypted: " + encrypted);
        String decrypted = encryptionAPI.symmetricDecryptString(encrypted);
        System.out.println("MSG decrypted: " + decrypted);
        assert !encrypted.equals(decrypted);
        assert msg.equals(decrypted);
    }

    @Test
    public void asymmetricEncryptionMsg() {
        String msg = "admin";
        String encrypted = encryptionAPI.asymmetricEncryptString(msg);
        String encrypted2 = encryptionAPI.asymmetricEncryptString(msg);
        System.out.println("MSG original: " + msg);
        System.out.println("MSG encrypted: " + encrypted);
        System.out.println("MSG encrypted 2: " + encrypted2);
        assert !encrypted.equals(msg);
        assert encrypted.equals(encrypted2);
    }

    @Test
    public void symmetricEncryptionFile() {
        byte[] msg = {1, 2, 3, 4, 5};
        byte[] encrypted = encryptionAPI.symmetricEncryptFile(msg);
        byte[] decrypted = encryptionAPI.symmetricDecryptFile(encrypted);
        System.out.println("MSG original: " + Arrays.toString(msg));
        System.out.println("MSG encrypted: " + Arrays.toString(encrypted));
        System.out.println("MSG decrypted: " + Arrays.toString(decrypted));
        assert !Arrays.equals(msg, encrypted);
        assert Arrays.equals(msg, decrypted);
    }
}
