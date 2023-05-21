package br.com.alexmdo.finantialcontrol.user.dto;

import br.com.alexmdo.finantialcontrol.user.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public static User fromCreateRequestDto(UserCreateRequestDto createRequest) {
        User user = new User();
        user.setFirstName(createRequest.firstName());
        user.setLastName(createRequest.firstName());
        user.setEmail(createRequest.firstName());
        return user;
    }

    public static User fromUpdateRequestDto(Long userId, UserUpdateRequestDto updateRequest) {
        User user = new User();
        user.setId(userId);
        user.setFirstName(updateRequest.firstName());
        user.setLastName(updateRequest.lastName());
        user.setEmail(updateRequest.email());
        return user;
    }

}
