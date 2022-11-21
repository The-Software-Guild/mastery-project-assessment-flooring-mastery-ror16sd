package com.sg.flooring.service;

import com.sg.flooring.dao.FlooringAuditDao;
import com.sg.flooring.dao.FlooringDao;
import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.dto.StateTax;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class FlooringServiceLayerImpl implements FlooringServiceLayer{

    private FlooringDao dao;
    private FlooringAuditDao adao;

    public FlooringServiceLayerImpl(FlooringDao dao, FlooringAuditDao adao){
        this.dao = dao;
        this.adao = adao;
    }

    @Override
    public Order getOrder(String orderNumber) throws FlooringPersistenceException {
        return dao.getOrder(orderNumber);
    }

    @Override
    public List<Order> listAllOrders(String date) throws FlooringPersistenceException {

        String orderFile = "Orders/Orders_"+date+".txt";

        if(!new File("Orders/Orders_"+date+".txt").exists()){
            throw new FlooringPersistenceException(orderFile+ " not found");
        }
        else{
            return dao.listAllOrders(date);
        }
    }

    @Override
    public Order addOrder(String[] orderInfo) throws FlooringPersistenceException, FlooringDataValidationException, FlooringAreaInsufficientException {
      //  String date = orderInfo[0];
      //  String name = orderInfo[1];
        String state = orderInfo[2];
        String type = orderInfo[3];
        BigDecimal area = new BigDecimal(orderInfo[4]);
        if (validateInfo(type,state, area)){
            Order addedOrder =  dao.addOrder(orderInfo);
            adao.writeAuditEntry("Order No. "+addedOrder.getOrderNumber()+" ADDED.");
            return addedOrder;
        }
        else{
            throw new FlooringDataValidationException("Data validation failure.");
        }

    }

    private boolean validateInfo(String type, String state, BigDecimal area) throws FlooringPersistenceException, FlooringDataValidationException, FlooringAreaInsufficientException {

        dao.loadProductsAndStateTax();
        if ((dao.listOfProducts().contains(type)) && (dao.listOfStates().contains(state))){
            if(area.compareTo(new BigDecimal(100)) >= 0)
                return true;
            else
                throw new FlooringAreaInsufficientException("Minimum Order Area of 100 sqr ft required");
        }
        else {
            throw new FlooringDataValidationException("Product Does Not Exist or No Service in State");
        }
    }

    public void writeOrderToFile(String date, Order newOrder) throws FlooringPersistenceException {
        dao.writeOrderToFile(date,newOrder);
    }

    @Override
    public void undoAddOrder(){
        dao.undoAddOrder();
    }

    @Override
    public Order removeOrder(String date,String orderNUmber) throws FlooringPersistenceException {
        Order removedOrder = dao.removeOrder(date,orderNUmber);
        adao.writeAuditEntry("Order No. "+removedOrder.getOrderNumber()+" REMOVED.");
        return removedOrder;
    }

    @Override
    public Order editOrder(String date, String orderNumber, String[] updates) throws FlooringPersistenceException, FlooringDataValidationException, FlooringAreaInsufficientException {
        String customerName = updates[0] ;
        String state = updates[1] ;
        String type = updates[2];
        String areaFromString = updates[3];
        BigDecimal area;
        String[] arr = date.split("/");
        String dateFormat = arr[0]+arr[1]+arr[2];
        listAllOrders(dateFormat);
        Order order = dao.getOrder(orderNumber);

        //check if any values are null
        if(customerName.equals("")){
            customerName = order.getCustomerName();
        }
        if (type.equals(""))
            type = order.getProductType();
        if (state.equals(""))
            state = order.getState();
        if (areaFromString.equals("")){
            area = order.getArea();
        }
        else{
            area = new BigDecimal(areaFromString);
        }



        if (validateInfo(type, state, area)){
            updates[0] = customerName;
            updates[1] = type;
            updates[2] = state;
            updates[3] = area.toString();
            Order editedOrder = dao.editOrder(date,orderNumber,updates);
            adao.writeAuditEntry("Order No. " + editedOrder.getOrderNumber()+" UPDATED.");
            return editedOrder;
        }
        else{
            throw new FlooringDataValidationException("Could not update order with information given");
        }

    }


    public void loadProductAndTaxInfo() throws FlooringPersistenceException {
        dao.loadProductsAndStateTax();
    }

    public List<Product> listAllProducts() throws FlooringPersistenceException {
        return dao.listAllProducts();
    }

    @Override
    public void loadOrderNumber() throws FlooringPersistenceException {
        dao.loadOrderNumber();
    }

    @Override
    public void writeOrderNumber() throws FlooringPersistenceException {
        dao.writeOrderNumber();
    }

    public void exportAllData() throws FlooringPersistenceException{
            dao.exportAllData();
    }
}
