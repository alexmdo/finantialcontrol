package br.com.alexmdo.finantialcontrol.domain.account.exception;

import br.com.alexmdo.finantialcontrol.infra.BusinessException;

public class AccountNotArchivedException extends BusinessException {

    public AccountNotArchivedException(String message) {
        super(message);
    }

}
