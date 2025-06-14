package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.BakeryInfo;

@Repository
public interface BakeryInfoRepository extends JpaRepository<BakeryInfo, Long> {
}
