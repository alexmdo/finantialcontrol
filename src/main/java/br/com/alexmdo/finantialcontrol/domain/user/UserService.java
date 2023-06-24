package br.com.alexmdo.finantialcontrol.domain.user;

import br.com.alexmdo.finantialcontrol.domain.user.exception.UserAlreadyRegisteredException;
import br.com.alexmdo.finantialcontrol.domain.user.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    @Transactional
    public CompletableFuture<User> createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyRegisteredException("Email already exists");
        }
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    @Async
    @Transactional
    public CompletableFuture<User> updateUser(User user) {
        var userFound = userRepository.findById(user.getId());

        if (userFound.isPresent()) {
            var existingUser = userFound.get();
            var hasEmailChanged = !existingUser.getEmail().equals(user.getEmail());

            if (hasEmailChanged && userRepository.existsByEmail(user.getEmail())) {
                throw new UserAlreadyRegisteredException("Email already exists");
            }
            return CompletableFuture.completedFuture(userRepository.save(user));
        } else {
            throw new UserNotFoundException("User not found given the id");
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Void> deleteUser(Long id, User user) {
        if (!Objects.equals(id, user.getId())) {
            throw new UserNotFoundException("User not found given the id");
        }
        userRepository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<User> getUserByIdAndUser(Long id, User user) {
        if (!Objects.equals(id, user.getId())) {
            throw new UserNotFoundException("User not found given the id");
        }
        return userRepository.findById(id)
                .map(CompletableFuture::completedFuture)
                .orElseThrow(() -> new UserNotFoundException("User not found given the id"));
    }

    @Async
    public CompletableFuture<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(CompletableFuture::completedFuture)
                .orElseThrow(() -> new UserNotFoundException("User not found given the email"));
    }

    @Async
    public CompletableFuture<Page<User>> getAllUsers(Pageable pageable) {
        return CompletableFuture.completedFuture(userRepository.findAll(pageable));
    }
}
