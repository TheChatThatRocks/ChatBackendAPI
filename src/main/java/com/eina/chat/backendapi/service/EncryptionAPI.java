package com.eina.chat.backendapi.service;

import com.eina.chat.backendapi.controller.CommandAPIController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

@Service
public class EncryptionAPI {
    /**
     * Logger
     */
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(EncryptionAPI.class);


    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.encryption.server.host}")
    String host;


    @Value("${app.encryption.server.port}")
    String port;

    /**
     * Encrypt with symmetric cipher
     *
     * @param toEncrypt object to encrypt
     * @return encrypted object
     */
    public String symmetricEncryptString(String toEncrypt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> request = new HttpEntity<>(toEncrypt, headers);
        HttpEntity<String> response = restTemplate.exchange("http://" + host + ":" + port + "/symmetricEncrypt", HttpMethod.POST, request, String.class);
        return response.getBody();
    }

    /**
     * Decrypt with symmetric cipher
     *
     * @param toDecrypt object to decrypt
     * @return decrypted object
     */
    public String symmetricDecryptString(String toDecrypt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> request = new HttpEntity<>(toDecrypt, headers);
        HttpEntity<String> response = restTemplate.exchange("http://" + host + ":" + port + "/symmetricDecrypt", HttpMethod.POST, request, String.class);
        return response.getBody();
    }

    /**
     * Encrypt with asymmetric cipher
     *
     * @param toEncrypt object to encrypt
     * @return encrypted object
     */
    public String asymmetricEncryptString(String toEncrypt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> request = new HttpEntity<>(toEncrypt, headers);
        HttpEntity<String> response = restTemplate.exchange("http://" + host + ":" +  port + "/asymmetricEncrypt", HttpMethod.POST, request, String.class);
        return response.getBody();
    }

    /**
     * Encrypt with symmetric cipher
     *
     * @param toEncrypt object to encrypt
     * @return encrypted object
     */
    public byte[] symmetricEncryptFile(byte[] toEncrypt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String toEncryptInString = Base64.getEncoder().encodeToString(toEncrypt);
        HttpEntity<String> request = new HttpEntity<>(toEncryptInString, headers);
        HttpEntity<String> response = restTemplate.exchange("http://" + host + ":" + port + "/symmetricEncrypt", HttpMethod.POST, request, String.class);
        if (response.getBody() != null)
            return Base64.getDecoder().decode(response.getBody());
        else
            return null;
    }


    /**
     * Decrypt with symmetric cipher
     *
     * @param toDecrypt object to decrypt
     * @return decrypted object
     */
    public byte[] symmetricDecryptFile(byte[] toDecrypt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String toDecryptInString = Base64.getEncoder().encodeToString(toDecrypt);
        HttpEntity<String> request = new HttpEntity<>(toDecryptInString, headers);
        HttpEntity<String> response = restTemplate.exchange("http://" + host + ":" + port + "/symmetricDecrypt", HttpMethod.POST, request, String.class);
        if (response.getBody() != null)
            return Base64.getDecoder().decode(response.getBody());
        else
            return null;
    }

}
