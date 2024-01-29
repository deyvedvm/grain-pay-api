package dev.deyve.grainpayapi.dtos;

public class Response {

    private Object data;

    private Integer status;

    private String message;

    public Response() {
    }

    public Response(Object data, Integer status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    // getters and setters

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
