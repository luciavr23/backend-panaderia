package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.UserAccount;
import com.example.enums.UserRole;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
	Optional<UserAccount> findByEmail(String email);

	boolean existsByEmailAndRole(String email, UserRole role);

	boolean existsByPhoneNumber(String phoneNumber);

	boolean existsByEmail(String email);

	Optional<UserAccount> findByResetToken(String resetToken);
}
