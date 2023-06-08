package br.com.alexmdo.finantialcontrol.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alexmdo.finantialcontrol.domain.user.exception.UserAlreadyRegisteredException;
import br.com.alexmdo.finantialcontrol.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyRegisteredException("Email already exists");
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        var userFound = userRepository.getReferenceById(user.getId());

        var hasEmailChanged = !userFound.getEmail().equals(user.getEmail());
        var isEmailAlreadyRegistered = userRepository.existsByEmail(user.getEmail());
        if (hasEmailChanged && isEmailAlreadyRegistered) {
            throw new UserAlreadyRegisteredException("Email already exists");
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id, User user) {
        if (id != user.getId()) {
            throw new UserNotFoundException("User not found given the id");
        }
        
        userRepository.deleteById(id);
    }

    public User getUserByIdAndUser(Long id, User user) {
        if (id != user.getId()) {
            throw new UserNotFoundException("User not found given the id");
        }

        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found given the id"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found given the email"));
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

}