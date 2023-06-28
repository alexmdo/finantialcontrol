package br.com.alexmdo.finantialcontrol.domain.category.exception;

import br.com.alexmdo.finantialcontrol.infra.BusinessException;

public class CategoryNotFoundException extends BusinessException {

    public CategoryNotFoundException(String message) {
        super(message);
    }

}
