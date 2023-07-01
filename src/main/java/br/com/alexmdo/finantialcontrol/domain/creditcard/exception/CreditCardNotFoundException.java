package br.com.alexmdo.finantialcontrol.domain.creditcard.exception;

import br.com.alexmdo.finantialcontrol.infra.BusinessException;

public class CreditCardNotFoundException extends BusinessException {

    public CreditCardNotFoundException(String message) {
        super(message);
    }

}
