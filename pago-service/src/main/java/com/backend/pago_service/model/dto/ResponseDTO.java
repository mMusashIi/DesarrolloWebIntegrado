package com.backend.pago_service.model.dto;

import lombok.Data;

@Data
public class ResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ResponseDTO<T> success(T data) {
        ResponseDTO<T> r = new ResponseDTO<>();
        r.success = true;
        r.data = data;
        return r;
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        ResponseDTO<T> r = new ResponseDTO<>();
        r.success = true;
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> ResponseDTO<T> error(String message) {
        ResponseDTO<T> r = new ResponseDTO<>();
        r.success = false;
        r.message = message;
        return r;
    }
}
