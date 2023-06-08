package br.com.alexmdo.finantialcontrol.domain.account;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alexmdo.finantialcontrol.domain.user.User;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByIdAndUser(Long id, User user);

    Page<Account> findAllByUser(Pageable pageable, User user);

}
