package com.haufe.test.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private int code;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
