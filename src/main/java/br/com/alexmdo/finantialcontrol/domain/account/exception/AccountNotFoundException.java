package br.com.alexmdo.finantialcontrol.domain.account.exception;

import br.com.alexmdo.finantialcontrol.infra.BusinessException;

public class AccountNotFoundException extends BusinessException {

    public AccountNotFoundException(String message) {
        super(message);
    }
    
}
