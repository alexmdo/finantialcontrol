package br.com.alexmdo.finantialcontrol.infra;

import org.springframework.security.core.context.SecurityContextHolder;

import br.com.alexmdo.finantialcontrol.domain.user.User;

public class BaseController {

    public User getPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
