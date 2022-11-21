package com.sg.flooring.ui;

import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;

import java.util.List;

public class FlooringView {
    private UserIO io;
    public FlooringView(UserIO io){this.io = io;}
    
    public void displayErrorMessage(String errorMag){
        io.print("=== ERROR ===");
        io.print(errorMag);
    }
    public void displayQuitMessage() {
        io.print("Goodbye!" + "\n");
    }

    public int printMenuAndGetSelection() {
        io.print("<<Flooring Program>>");
        io.print("1. Display Orders");
        io.print("2. Add an Order");
        io.print("3. Edit an Order");
        io.print("4. Remove an Order");
        io.print("5. Export All Data");
        io.print("6. Exit");
        return io.readInt("Please select from the menu");
    }

    public void displayUnknownCommandBanner() { io.print("\nInvalid input. Please input 1, 2, 3, 4, 5, or 6" + "\n");}

    public String displayGetOrdersByDate() {

        String stringDate = io.readString("Enter date of the order(s) - mm/dd/yyyy: ");
        String[] arr = stringDate.split("/");
        return arr[0]+arr[1]+arr[2];
    }

    public void displayOrderList(List<Order> orderList) {
        for (Order currentOrder : orderList){
            io.print("Customer Name: " + currentOrder.getCustomerName() +
                    "\t\t\tOrder No. " + currentOrder.getOrderNumber());
            io.print("State: " + currentOrder.getState());
            io.print("Tax Rate: " + currentOrder.getTaxRate());
            io.print("Product Type: " + currentOrder.getProductType());
            io.print("Area: " + currentOrder.getArea());
            io.print("Cost Per Square Foot: $" + currentOrder.getCostPerSqrFt());
            io.print("Labor Cost Per Square Foot: $" + currentOrder.getLaborCostPerSqrFt());
            io.print("Labor Cost: $" + currentOrder.getLaborCost());
            io.print("Material Cost: $" + currentOrder.getMaterialCost());
            io.print("Tax: $" + currentOrder.getTax());
            io.print("Total: $" + currentOrder.getTotal());
            io.print("---------------------------------");
        }
    }

    public String[] getNewOrderInfo() {
       String date = io.readString("Enter order date: ");
       String name = io.readString("Enter customer name: ");
       String state = io.readString("Enter state: ");
       String type = io.readString("Enter product type: ");
       String area = io.readString("Enter flooring area: ");

       String [] newOrder = new String[]{date, name, state, type, area};
       return newOrder;
    }

    public void printProductBanner() {
        io.print("---PRODUCT MENU---");
        io.print("Type\t\t\tCost Per Sqr Ft\t\t\tLabor Per Sqr Ft");
        io.print("----------------------------------------------------------");
    }
    public void displayProduct(Product product){
        io.print(product.getType() + "\t\t\t" + product.getCostPerSgrFt() + "\t\t\t" + product.getLaborCostPerSqrFt());
    }

    public boolean displayOrderConfirmation(String date, Order currentOrder) {
        io.print("---ORDER CONFIRMATION---");
        io.print("Customer Name: " + currentOrder.getCustomerName() +
                "\t\t\tOrder No. " + currentOrder.getOrderNumber());
        io.print("State: " + currentOrder.getState());
        io.print("Tax Rate: " + currentOrder.getTaxRate());
        io.print("Product Type: " + currentOrder.getProductType());
        io.print("Area: " + currentOrder.getArea());
        io.print("Cost Per Square Foot: $" + currentOrder.getCostPerSqrFt());
        io.print("Labor Cost Per Square Foot: $" + currentOrder.getLaborCostPerSqrFt());
        io.print("Labor Cost: $" + currentOrder.getLaborCost());
        io.print("Material Cost: $" + currentOrder.getMaterialCost());
        io.print("Tax: $" + currentOrder.getTax());
        io.print("Total: $" + currentOrder.getTotal());
        io.print("---------------------------------");

        String IsConfirmed = io.readString("Would you like to confirm your order for " + date+ " ? Y/N: ");
        if (IsConfirmed.toLowerCase().equals("y")){
            return true;
        }
        else
            return false;
    }

    public void displayOrderCancelled() {
        io.print("---ORDER CANCELLED---");
    }

    public String[] displayRemoveOrderInfo() {
        String date = io.readString("Enter order date: ");
        String orderNum = io.readString("Enter order number: ");

        String[] result = new String[2];
        result[0] = date;
        result[1] = orderNum;
        return result;
    }

    public void displayOrderRemoved(Order order) {
        io.print("Order No. " + order.getOrderNumber() + " removed!");
    }

    public String[] displayEditOrderInfo() {
        io.print("---EDIT ORDER---");
        String date = io.readString("Enter order date: ");
        String orderNum = io.readString("Enter order number: ");

        String[] result = new String[2];
        result[0] = date;
        result[1] = orderNum;
        return result;
    }

    public String[] displayUpdateOrderInfo(Order order, List<Product> products) {
        String customerName = io.readString("Enter customer name ("+order.getCustomerName()+"): ");
        String state = io.readString("Enter state ("+order.getState()+"): ");
        io.print("");
        printProductBanner();
        for(Product product: products){
            displayProduct(product);
        }
        String productType = io.readString("Enter product type ("+order.getProductType()+"): ");
        String area = io.readString("Enter area ("+order.getArea()+"): ");

        String[] updates = new String[4];
       updates[0] = customerName;
       updates[1] = state;
       updates[2] = productType;
       updates[3] = area;
       return updates;
    }

    public void displayExportData() {
        io.print("---Exporting All Existing Orders on File.---");
    }
}

