package com.mocha.shopwebsite.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketRepository extends JpaRepository<Basket, Integer> {

    List<Basket> findByUserId(Integer userId);
    
    void deleteByItemId(Long itemId);
    
    void deleteAllByItemId(Integer itemId);

}
