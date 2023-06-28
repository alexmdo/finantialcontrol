package br.com.alexmdo.finantialcontrol.domain.user.exception;

import br.com.alexmdo.finantialcontrol.infra.BusinessException;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
