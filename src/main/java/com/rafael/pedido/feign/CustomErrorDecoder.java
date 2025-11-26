package com.rafael.pedido.feign;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400) {
            return new RuntimeException("Error Feign " + response.status());
        }
        return new ErrorDecoder.Default().decode(methodKey, response);
    }
}
