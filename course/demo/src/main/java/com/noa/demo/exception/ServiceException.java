package com.noa.demo.exception;



public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;

    public ServiceException(String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }

    public ServiceException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public ServiceException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
