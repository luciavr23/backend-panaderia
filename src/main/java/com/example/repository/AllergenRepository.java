package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Allergen;

@Repository
public interface AllergenRepository extends JpaRepository<Allergen, Long> {
}
