package com.sg.flooring.service;

import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.dto.StateTax;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FlooringServiceLayer {
    Order getOrder(String orderNumber) throws FlooringPersistenceException;
    List<Order> listAllOrders(String date) throws FlooringPersistenceException;
    Order addOrder(String[] orderInfo) throws FlooringPersistenceException, FlooringDataValidationException, FlooringAreaInsufficientException;
    Order removeOrder(String date,String orderNumber) throws FlooringPersistenceException;
    Order editOrder(String date, String orderNumber, String[] updates) throws FlooringPersistenceException, FlooringDataValidationException, FlooringAreaInsufficientException;


    void loadProductAndTaxInfo() throws FlooringPersistenceException;
     List<Product> listAllProducts() throws FlooringPersistenceException;

     void writeOrderToFile(String date, Order newOrder) throws FlooringPersistenceException;
     void undoAddOrder();
     void loadOrderNumber() throws FlooringPersistenceException;
     void writeOrderNumber() throws FlooringPersistenceException;

    void exportAllData() throws FlooringPersistenceException;
}

