package br.com.alexmdo.finantialcontrol.domain.account.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }
    
}
