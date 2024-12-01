package com.assignment.product_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.product_service.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {}