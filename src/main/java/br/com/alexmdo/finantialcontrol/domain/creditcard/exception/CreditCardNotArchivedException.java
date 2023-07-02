package br.com.alexmdo.finantialcontrol.domain.creditcard.exception;

import br.com.alexmdo.finantialcontrol.infra.BusinessException;

public class CreditCardNotArchivedException extends BusinessException {
    public CreditCardNotArchivedException(String message) {
        super(message);
    }

}
