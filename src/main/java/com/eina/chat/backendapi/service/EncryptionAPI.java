package com.eina.chat.backendapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EncryptionAPI {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.encryption.url}")
    String url;


    @Value("${spring.encryption.port}")
    String port;
    /**
     * Encrypt with symmetric cipher
     *
     * @param toEncrypt object to encrypt
     * @return encrypted object
     */
    public String symmetricEncryptString(String toEncrypt) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.TEXT_PLAIN);
//        HttpEntity<String> request = new HttpEntity<>(toEncrypt, headers);
//         restTemplate.exchange(url, HttpMethod.POST, request, String.class);
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
