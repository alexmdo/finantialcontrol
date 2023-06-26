package br.com.alexmdo.finantialcontrol.domain.user;

import br.com.alexmdo.finantialcontrol.domain.user.exception.UserAlreadyRegisteredException;
import br.com.alexmdo.finantialcontrol.domain.user.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public CompletableFuture<User> createUserAsync(User user) {
        return CompletableFuture.supplyAsync(() -> {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new UserAlreadyRegisteredException("Email already exists");
            }

            return userRepository.save(user);
        });
    }

    @Transactional
    public CompletableFuture<User> updateUserAsync(User user) {
        return CompletableFuture
                .supplyAsync(() -> userRepository.findById(user.getId()))
                .thenApply(userFound -> {
                    if (userFound.isEmpty()) {
                        throw new UserNotFoundException("User not found given the id");
                    }

                    var existingUser = userFound.get();
                    var hasEmailChanged = !existingUser.getEmail().equals(user.getEmail());

                    if (hasEmailChanged && userRepository.existsByEmail(user.getEmail())) {
                        throw new UserAlreadyRegisteredException("Email already exists");
                    }

                    return userRepository.save(user);
                });
    }

    @Transactional
    public CompletableFuture<Void> deleteUserAsync(Long id, User user) {
        return CompletableFuture.runAsync(() -> {
            if (!Objects.equals(id, user.getId())) {
                throw new UserNotFoundException("User not found given the id");
            }

            userRepository.deleteById(id);
        });
    }

    public CompletableFuture<User> getUserByIdAndUserAsync(Long id, User user) {
        return CompletableFuture.supplyAsync(() -> {
            if (!Objects.equals(id, user.getId())) {
                throw new UserNotFoundException("User not found given the id");
            }

            return userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found given the id"));
        });
    }

    public CompletableFuture<User> getUserByEmailAsync(String email) {
        return CompletableFuture.supplyAsync(() -> userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found given the email")));
    }

    public CompletableFuture<Page<User>> getAllUsersAsync(Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> userRepository.findAll(pageable));
    }
}
