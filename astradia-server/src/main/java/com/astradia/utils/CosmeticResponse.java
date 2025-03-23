package com.astradia.utils;

import com.astradia.enums.ResponseType;

public class CosmeticResponse {
    private final ResponseType code;
    private final String message;

    private CosmeticResponse(ResponseType responseType, String message) {
        this.code = responseType;
        this.message = message;
    }

    public static CosmeticResponse of(ResponseType code, String message) {
        return new CosmeticResponse(code, message);
    }

    public static CosmeticResponse of(ResponseType code) {
        return new CosmeticResponse(code, null);
    }

    public ResponseType getCode() {
        return code;
    }

    public String getMessage() {
        return message == null ? code.name() : message;
    }
}
