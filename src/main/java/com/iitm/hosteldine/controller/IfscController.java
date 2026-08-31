package com.iitm.hosteldine.controller;

import com.iitm.hosteldine.service.IfscCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "${url.ifsc}")
@RequiredArgsConstructor
public class IfscController {

    private final IfscCodeService ifscCodeService;

    @GetMapping(value = "${url.ifsc.code}")
    public ResponseEntity<?> validateIfscCode(@PathVariable("ifscCode") String ifscCode) {
        Map<String, Object> response = ifscCodeService.validateIfscCode(ifscCode);
        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}

