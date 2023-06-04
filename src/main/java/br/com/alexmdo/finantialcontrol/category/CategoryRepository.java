package br.com.alexmdo.finantialcontrol.category;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alexmdo.finantialcontrol.user.User;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);

    Page<Category> findAllByUser(Pageable pageable, User user);

    Optional<Category> findByIdAndUser(Long id, User user);    
}
