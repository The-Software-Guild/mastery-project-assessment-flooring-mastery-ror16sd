package com.sg.flooring.dao;

import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.dto.StateTax;
import com.sg.flooring.service.FlooringPersistenceException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FlooringDao {
    Order getOrder(String orderNumber) throws FlooringPersistenceException;
    List<Order> listAllOrders(String date) throws FlooringPersistenceException;
    Order addOrder(String[] orderInfo) throws FlooringPersistenceException;
    Order removeOrder(String date,String orderNumber) throws FlooringPersistenceException;
    Order editOrder (String date,String orderNumber, String[] updates) throws FlooringPersistenceException;
    void loadProductsAndStateTax() throws FlooringPersistenceException;

    List<String> listOfStates()throws FlooringPersistenceException;

    List<String> listOfProducts()throws FlooringPersistenceException;

    List<Product> listAllProducts()throws FlooringPersistenceException;

    void writeOrderToFile(String date, Order newOrder) throws FlooringPersistenceException;

    void undoAddOrder();

    void writeOrderNumber() throws  FlooringPersistenceException;
    void loadOrderNumber() throws FlooringPersistenceException;

    void exportAllData() throws FlooringPersistenceException;
}
