package br.com.alexmdo.finantialcontrol.domain.user;

import br.com.alexmdo.finantialcontrol.infra.BusinessException;
import br.com.alexmdo.finantialcontrol.domain.user.exception.UserAlreadyRegisteredException;
import br.com.alexmdo.finantialcontrol.domain.user.exception.UserNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    @CircuitBreaker(name = "createUser", fallbackMethod = "createUserFallback")
    @TimeLimiter(name = "createUser")
    public CompletableFuture<User> createUserAsync(User user) {
        return CompletableFuture.supplyAsync(() -> {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new UserAlreadyRegisteredException("Email already exists");
            }

            return userRepository.save(user);
        });
    }

    @Transactional
    @CircuitBreaker(name = "updateUser", fallbackMethod = "updateUserFallback")
    @TimeLimiter(name = "updateUser")
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
    @CircuitBreaker(name = "deleteUser", fallbackMethod = "deleteUserFallback")
    @TimeLimiter(name = "deleteUser")
    public CompletableFuture<Void> deleteUserAsync(Long id, User user) {
        return CompletableFuture.runAsync(() -> {
            if (!Objects.equals(id, user.getId())) {
                throw new UserNotFoundException("User not found given the id");
            }

            userRepository.deleteById(id);
        });
    }

    @CircuitBreaker(name = "getUserByIdAndUser", fallbackMethod = "getUserByIdAndUserFallback")
    @TimeLimiter(name = "getUserByIdAndUser")
    public CompletableFuture<User> getUserByIdAndUserAsync(Long id, User user) {
        return CompletableFuture.supplyAsync(() -> {
            if (!Objects.equals(id, user.getId())) {
                throw new UserNotFoundException("User not found given the id");
            }

            return userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found given the id"));
        });
    }

    @CircuitBreaker(name = "getUserByEmail", fallbackMethod = "getUserByEmailFallback")
    @TimeLimiter(name = "getUserByEmail")
    public CompletableFuture<User> getUserByEmailAsync(String email) {
        return CompletableFuture.supplyAsync(() -> userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found given the email")));
    }

    @CircuitBreaker(name = "getAllUsers", fallbackMethod = "getAllUsersFallback")
    @TimeLimiter(name = "getAllUsers")
    public CompletableFuture<Page<User>> getAllUsersAsync(Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> userRepository.findAll(pageable));
    }

    public CompletableFuture<User> createUserFallback(User user, Throwable throwable) {
        // Fallback logic for createUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for createUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    public CompletableFuture<User> updateUserFallback(User user, Throwable throwable) {
        // Fallback logic for updateUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for updateUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Void> deleteUserFallback(Long id, User user, Throwable throwable) {
        // Fallback logic for deleteUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for deleteUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<User> getUserByIdAndUserFallback(Long id, User user, Throwable throwable) {
        // Fallback logic for getUserByIdAndUserAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for getUserByIdAndUserAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<User> getUserByEmailFallback(String email, Throwable throwable) {
        // Fallback logic for getUserByEmailAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for getUserByEmailAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

    public CompletableFuture<Page<User>> getAllUsersFallback(Pageable pageable, Throwable throwable) {
        // Fallback logic for getAllUsersAsync
        if (throwable instanceof BusinessException) {
            throw (BusinessException) throwable;
        } else {
            // Handle other types of exceptions or fallback behavior
            // Return a default or fallback value, or perform alternative logic
            log.error("Fallback triggered for getAllUsersAsync due to: " + throwable.getMessage());
            return CompletableFuture.completedFuture(null); // Return a default or fallback value
        }
    }

}
