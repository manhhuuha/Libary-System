package org.example.thuvien.repository;

import org.example.thuvien.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Để Spring tự tìm chính xác user theo username dưới DB
    Optional<User> findByUsername(String username);
}