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
        byte[] msg = {56, -26, 35, -20, 102, 73, -45, 66, -69, -57, 25, -57, 66, 12, -99, 101, 84, -22, -37, -2, 108,
                106, -23, -12, 18, -35, -111, -126, 29, -8, -96, -3, -91, 11, 97, 53, -15, 30, 88, 14, -23, 56, -88, 110,
                46, 48, 9, 3, -117, 7, 90, -38, -47, -70, -40, -60, -19, -63, -127, 78, -64, 53, 21, -91, 105, -51, 115,
                42, -53, -56, 53, 24, -93, 2, -6, 113, 108, -29, 16, 74, -105, -45, -94, -100, 100, 110, 95, 49, 120,
                -119, 64, 81, -98, 77, 24, 32, -100, 71, -16, -14};

        byte[] encrypted = encryptionAPI.symmetricEncryptFile(msg);
        byte[] decrypted = encryptionAPI.symmetricDecryptFile(encrypted);
        System.out.println("MSG original: " + Arrays.toString(msg));
        System.out.println("MSG encrypted: " + Arrays.toString(encrypted));
        System.out.println("MSG decrypted: " + Arrays.toString(decrypted));
        assert !Arrays.equals(msg, encrypted);
        assert Arrays.equals(msg, decrypted);
    }
}
