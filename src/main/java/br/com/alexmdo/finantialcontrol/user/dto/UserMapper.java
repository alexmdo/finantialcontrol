package br.com.alexmdo.finantialcontrol.user.dto;

import org.springframework.stereotype.Component;

import br.com.alexmdo.finantialcontrol.user.User;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequestDto createRequestDto) {
        return new User(null, createRequestDto.firstName(), createRequestDto.lastName(), createRequestDto.email());
    }

    public User updateEntity(User existingUser, UserUpdateRequestDto updateRequestDto) {
        return new User(
                existingUser.getId(),
                updateRequestDto.firstName(),
                updateRequestDto.lastName(),
                updateRequestDto.email()
        );
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
    
}
