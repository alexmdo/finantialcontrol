package br.com.alexmdo.finantialcontrol.domain.user.exception;

import br.com.alexmdo.finantialcontrol.infra.BusinessException;

public class UserAlreadyRegisteredException extends BusinessException {

    public UserAlreadyRegisteredException(String message) {
        super(message);
    }

}
