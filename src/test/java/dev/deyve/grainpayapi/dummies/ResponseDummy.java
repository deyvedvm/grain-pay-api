package dev.deyve.grainpayapi.dummies;

import dev.deyve.grainpayapi.dtos.Response;

public class ResponseDummy {

    public static Response buildResponse(Object object, int status, String message) {
        return new Response(object, status, message);
    }
}
