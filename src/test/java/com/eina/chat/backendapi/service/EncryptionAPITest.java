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
        String encrypted = encryptionAPI.symmetricEncryptString(msg);
        System.out.println("MSG encrypted: " + encrypted);
        String decrypted = encryptionAPI.symmetricDecryptString(encrypted);
        System.out.println("MSG decrypted: " + decrypted);
        assert !encrypted.equals(decrypted);
        assert msg.equals(decrypted);
    }

    @Test
    public void asymmetricEncryptionMsg() {
        //TODO: test asymetric
//        String msg = "testMsgASYM";
//        String encrypted = encryptionAPI.asymmetricEncryptString(msg);
//        System.out.println("MSG encrypted: " + encrypted);
//        String decrypted = encryptionAPI.symmetricDecryptString(encrypted);
//        System.out.println("MSG decrypted: " + decrypted);
//        assert !encrypted.equals(decrypted);
//        assert msg.equals(decrypted);
    }

    @Test
    public void symmetricEncryptionFile() {

        byte[] msg = ("testFILESYM").getBytes();
        byte[] encrypted = encryptionAPI.symmetricEncryptFile(msg);
        System.out.println("MSG encrypted: " + new String(encrypted));
        String decrypted = new String(encryptionAPI.symmetricDecryptFile(encrypted));
        System.out.println("MSG decrypted: " + decrypted);
        assert !new String(encrypted).equals(decrypted);
        assert new String(msg).equals(decrypted);
    }

}
