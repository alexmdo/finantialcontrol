package br.com.alexmdo.finantialcontrol.user;

import org.springframework.stereotype.Component;

import br.com.alexmdo.finantialcontrol.user.dto.UserCreateRequestDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserUpdateRequestDto;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequestDto createRequestDto) {
        return new User(null, createRequestDto.firstName(), createRequestDto.lastName(), createRequestDto.email(), createRequestDto.password());
    }

    public User updateEntity(User existingUser, UserUpdateRequestDto updateRequestDto) {
        if (updateRequestDto.firstName() != null) {
            existingUser.setFirstName(updateRequestDto.firstName());
        }

        if (updateRequestDto.lastName() != null) {
            existingUser.setLastName(updateRequestDto.lastName());
        }

        if (updateRequestDto.email() != null) {
            existingUser.setEmail(updateRequestDto.email());
        }

        if (updateRequestDto.password() != null) {
            existingUser.setPassword(updateRequestDto.password());
        }

        return existingUser;
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
    
}
