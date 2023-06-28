package br.com.alexmdo.finantialcontrol.infra;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
