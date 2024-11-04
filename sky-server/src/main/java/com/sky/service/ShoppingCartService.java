package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addShoppingCart(ShoppingCartDTO shop);

    List<ShoppingCart> showShoppingCart();

    void deleteShoppingCart();

    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
