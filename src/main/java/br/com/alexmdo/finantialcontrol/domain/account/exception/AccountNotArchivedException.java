package br.com.alexmdo.finantialcontrol.domain.account.exception;

public class AccountNotArchivedException extends RuntimeException {

    public AccountNotArchivedException(String message) {
        super(message);
    }

}
