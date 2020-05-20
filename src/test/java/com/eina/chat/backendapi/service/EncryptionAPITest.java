package com.eina.chat.backendapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EncryptionAPITest {

    @Autowired
    EncryptionAPI encryptionAPI;

    @Test
    public void symmetricEncryptionMsg() {

        String msg = "testMsgSYM";
//        String encrypted = encryptionAPI.symmetricEncryptString(msg);
//        String decrypted = encryptionAPI.symmetricDecryptString(encrypted);
//        assert msg.equals(decrypted);
    }

    @Test
    public void asymmetricEncryptionMsg() {
        String msg = "testMsgASYM";
//        String encrypted = encryptionAPI.asymmetricEncryptString(msg);
//        String decrypted = encryptionAPI.asymmetricEncryptString(encrypted);
//        assert msg.equals(decrypted);
    }

    @Test
    public void symmetricEncryptionFile() {

        byte[] msg = ("testMsgSYM").getBytes();
//        byte[] encrypted = encryptionAPI.symmetricEncryptFile(msg);
//        byte[] decrypted = encryptionAPI.symmetricDecryptFile(encrypted);
//        assert new String(msg).equals(new String(decrypted));
    }

    @Test
    public void asymmetricEncryptionFile() {
        byte[] msg = ("testMsgASYM").getBytes();
//        String encrypted = encryptionAPI.asymmetricEncryptFile(msg);
//        String decrypted = encryptionAPI.asymmetricEncryptFile(encrypted);
//        assert new String(msg).equals(new String(decrypted));
    }
}
