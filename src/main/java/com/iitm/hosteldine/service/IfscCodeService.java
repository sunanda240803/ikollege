package com.iitm.hosteldine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IfscCodeService {

    private final WebClient webClient;

    @Value("${url.validate.ifsc.code}")
    private String validateURL;

    public Map<String, Object> validateIfscCode(String ifscCode) {
        String url = validateURL + ifscCode;
        try{
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response;
        }
        catch (WebClientResponseException e){
                return Collections.emptyMap();
        }
    }
}
