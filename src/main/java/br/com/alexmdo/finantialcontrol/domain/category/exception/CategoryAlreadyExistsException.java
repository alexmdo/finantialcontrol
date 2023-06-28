package br.com.alexmdo.finantialcontrol.domain.category.exception;

import br.com.alexmdo.finantialcontrol.infra.BusinessException;

public class CategoryAlreadyExistsException extends BusinessException {

    public CategoryAlreadyExistsException(String message) {
        super(message);
    }

}
