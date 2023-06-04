package br.com.alexmdo.finantialcontrol.category;

import org.springframework.security.core.context.SecurityContextHolder;

import br.com.alexmdo.finantialcontrol.user.User;

public class BaseController {

    public User getPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
