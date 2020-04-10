package com.eina.chat.backendapi.service;

import org.springframework.stereotype.Service;

@Service
public class EncryptionAPI {
    /**
     * Encrypt with symmetric cipher
     *
     * @param toEncrypt object to encrypt
     * @return encrypted object
     */
    public String symmetricEncryptString(String toEncrypt) {
        // TODO:
        return toEncrypt;
    }

    /**
     * Decrypt with symmetric cipher
     *
     * @param toDecrypt object to decrypt
     * @return decrypted object
     */
    public String symmetricDecryptString(String toDecrypt) {
        // TODO:
        return toDecrypt;
    }

    /**
     * Encrypt with asymmetric cipher
     *
     * @param toEncrypt object to encrypt
     * @return encrypted object
     */
    public String asymmetricEncryptString(String toEncrypt) {
        // TODO:
        return toEncrypt;
    }

    /**
     * Encrypt with symmetric cipher
     *
     * @param toEncrypt object to encrypt
     * @return encrypted object
     */
    public byte[] symmetricEncryptFile(byte[] toEncrypt) {
        // TODO:
        return toEncrypt;
    }


    /**
     * Decrypt with symmetric cipher
     *
     * @param toDecrypt object to decrypt
     * @return decrypted object
     */
    public byte[] symmetricDecryptFile(byte[] toDecrypt) {
        // TODO:
        return toDecrypt;
    }

}
