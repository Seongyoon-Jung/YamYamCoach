package com.yamyam.diet.repository;

import com.yamyam.diet.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {
    // 기본적인 CRUD 기능은 JpaRepository에서 제공됨
} 