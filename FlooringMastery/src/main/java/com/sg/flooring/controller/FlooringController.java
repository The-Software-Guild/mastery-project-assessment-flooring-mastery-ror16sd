package com.sg.flooring.controller;

import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.service.FlooringAreaInsufficientException;
import com.sg.flooring.service.FlooringDataValidationException;
import com.sg.flooring.service.FlooringPersistenceException;
import com.sg.flooring.service.FlooringServiceLayer;
import com.sg.flooring.ui.FlooringView;
import com.sg.flooring.ui.UserIO;
import com.sg.flooring.ui.UserIOConsoleImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FlooringController {
    private UserIO io = new UserIOConsoleImpl();
    private FlooringView view;
    private FlooringServiceLayer service;

    public FlooringController(FlooringView view, FlooringServiceLayer service){
        this.view = view;
        this.service = service;
    }

    public void run(){
        boolean keepGoing = true;
        int menuSelection = 0;
        try{
            loadProductAndTaxInformation();
            loadOrderNumbers();
            while(keepGoing){
                menuSelection = getMenuSelection();
                switch(menuSelection){
                    case 1:
                        displayOrders();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        removeOrder();
                        break;
                    case 5:
                        exportAllData();
                        break;
                    case 6:
                        keepGoing = false;
                        break;
                    default:
                        unknownCommand();
                }
            }
            saveOrderNumber();
        } catch (FlooringPersistenceException | FlooringDataValidationException | FlooringAreaInsufficientException e){
            view.displayErrorMessage(e.getMessage());
        }
        view.displayQuitMessage();
    }

    private void saveOrderNumber() throws FlooringPersistenceException {
        service.writeOrderNumber();
    }

    private void loadOrderNumbers() throws FlooringPersistenceException {
        service.loadOrderNumber();
    }

    private void loadProductAndTaxInformation() throws FlooringPersistenceException {
        service.loadProductAndTaxInfo();
    }

    private void unknownCommand() {view.displayUnknownCommandBanner();}

    private void exportAllData() throws FlooringPersistenceException {
        view.displayExportData();
        service.exportAllData();
    }

    private void removeOrder() throws FlooringPersistenceException {
        String[] dateAndOrderNumber = view.displayRemoveOrderInfo();
        String[] arr = dateAndOrderNumber[0].split("/");
        String date = arr[0]+arr[1]+arr[2];
        //load orders
        service.listAllOrders(date);
        Order order = service.removeOrder(dateAndOrderNumber[0],dateAndOrderNumber[1]);
        view.displayOrderRemoved(order);
    }

    private void editOrder() throws FlooringPersistenceException, FlooringDataValidationException, FlooringAreaInsufficientException {
        String[] dateAndOrderNumber = view.displayEditOrderInfo();
        String[] arr = dateAndOrderNumber[0].split("/");
        String date = arr[0]+arr[1]+arr[2];
        //load orders
        service.listAllOrders(date);
        List<Product> products = service.listAllProducts();
        Order order = service.getOrder(dateAndOrderNumber[1]);
        String[] updates = view.displayUpdateOrderInfo(order,products);
//        updates[0] = customerName;
//        updates[1] = state;
//        updates[2] = productType;
//        updates[3] = area;
        service.editOrder(dateAndOrderNumber[0],dateAndOrderNumber[1],updates);

    }

    private void productMenu() throws FlooringPersistenceException{
        try {
            view.printProductBanner();
            for (Product p : service.listAllProducts()) {
                view.displayProduct(p);
            }
        }catch(FlooringPersistenceException e){
            throw new FlooringPersistenceException(e.getMessage());
        }
    }

    //print product menu and collect user input, send input to service layer to check if
    //valid use of product and state. Order object created with unique order number from file
    //Order sent to be written to file
    private void addOrder() throws FlooringPersistenceException, FlooringDataValidationException, FlooringAreaInsufficientException {
        productMenu();

        String[] orderInfo = view.getNewOrderInfo();
        String date = orderInfo[0];
        //data validation and creation of the order
        Order newOrder = service.addOrder(orderInfo);
        if (view.displayOrderConfirmation(date,newOrder))
            //also update last order number to previous value to unwind steps. current - 1;
            service.writeOrderToFile(date,newOrder);
        else {
            view.displayOrderCancelled();
            service.undoAddOrder();
        }
    }

    private void displayOrders() throws FlooringPersistenceException{
        String date = view.displayGetOrdersByDate();
        List<Order> orderList = service.listAllOrders(date);
        view.displayOrderList(orderList);
    }

    private int getMenuSelection() {return view.printMenuAndGetSelection();}
}
