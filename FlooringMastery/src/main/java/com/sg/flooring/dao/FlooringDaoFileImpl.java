package com.sg.flooring.dao;

import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.dto.StateTax;
import com.sg.flooring.service.FlooringPersistenceException;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class FlooringDaoFileImpl implements FlooringDao{

    private static final String DEMLINITER = ",";

    Map<Integer,Order> orders = new HashMap<>();
    Map<String, Product> products = new HashMap<>();
    Map<String, StateTax> stateTaxes = new HashMap<>();
    String productFile = "Data/Products.txt";
     String stateTaxFile = "Data/Taxes.txt";

    private int orderNumber;
    public int getOrderNumber() {return orderNumber;};

    public FlooringDaoFileImpl(String productFile, String stateTaxFile){
        this.productFile = productFile;
        this.stateTaxFile = stateTaxFile;
    }

    public FlooringDaoFileImpl(){}

    @Override
    public Order getOrder(String orderNumber) throws FlooringPersistenceException {
        return orders.get(Integer.parseInt(orderNumber));
    }

    @Override
    public List<Order> listAllOrders(String date) throws FlooringPersistenceException {

        String orderFile = "Orders/Orders_"+date+".txt";
        orders = readOrdersFromFile(orderFile);
        return new ArrayList<Order>(orders.values());
    }

    @Override
    public Order addOrder(String[] orderInfo) throws FlooringPersistenceException {

        String date = orderInfo[0];
        String name = orderInfo[1];
        String state = orderInfo[2];
        String type = orderInfo[3];
        BigDecimal area = new BigDecimal(orderInfo[4]);

        Product productInfo = products.get(type);
        Product product = new Product(type, area,productInfo.getCostPerSgrFt(),productInfo.getLaborCostPerSqrFt());
        StateTax stateInfo = stateTaxes.get(state);

        return new Order(nextOrderNumber(),name,stateInfo, product);
    }

    //order number is incremented when a new order is being created but before order confirmation. If order NOT
    //confirmed, this will take oder number to original value since no order was placed.
    public void undoAddOrder() {
        orderNumber--;
    }
    public void writeOrderToFile(String date, Order order) throws FlooringPersistenceException {
        String [] temp = date.split("/");
        String formatDate = temp[0]+temp[1]+temp[2];

        PrintWriter out;
        try{
            out = new PrintWriter(new FileWriter("Orders/Orders_"+formatDate+".txt",true));
        } catch (IOException e) {
            throw new FlooringPersistenceException("Could not write order to file", e);
        }

        String orderAsText = marshallOrder(order);
        out.println(orderAsText);
        out.flush();
        out.close();
    }

    private String marshallOrder(Order order) {
        return order.getOrderNumber() + DEMLINITER + order.getCustomerName() + DEMLINITER +
                order.getState() + DEMLINITER + order.getTaxRate() + DEMLINITER + order.getProductType() + DEMLINITER +
                order.getArea() + DEMLINITER + order.getCostPerSqrFt() + DEMLINITER + order.getLaborCostPerSqrFt();
    }

    @Override
    public Order removeOrder( String date, String orderNumber) throws FlooringPersistenceException {
        String[] arr = date.split("/");
        String dateFormat = arr[0]+arr[1]+arr[2];
        int orderNum = Integer.parseInt(orderNumber);
        Order removedOrder;
        if(orders.containsKey(orderNum)){
            removedOrder = orders.get(orderNum);
            orders.remove(orderNum);

            PrintWriter out;
            //clear order file to write updated order list since writeToFile only appends
            try {
                out = new PrintWriter(new FileWriter("Orders/Orders_" + dateFormat + ".txt"));
            } catch(IOException e) {
                throw new FlooringPersistenceException("Could not update file with removed order", e);
            }
           // out.print("");
            out.flush();
            out.close();

            for(Order order : orders.values()){
                writeOrderToFile(date,order);
            }
        }
        else {
            throw new FlooringPersistenceException("Order_" + date + ".txt does not exist or does not contain " + "order number " + orderNumber);
        }
        return removedOrder;
    }

    @Override
    public Order editOrder(String date, String orderNumber, String[] updates) throws FlooringPersistenceException {
        String customerName = updates[0];
        String state = updates[2] ;
        String type = updates[1];
        BigDecimal area = new BigDecimal(updates[3]);
        //load orders based on date
        String[] arr = date.split("/");
        String dateFormat = arr[0]+arr[1]+arr[2];
        listAllOrders(dateFormat);
        Order order = getOrder(orderNumber);
        for(int i = 0; i < updates.length; i++){
           //update order values
                if(i == 0){
                    order.setCustomerName(customerName);
                } else if (i == 2) {
                    listOfStates();
                    order.setStateTax(new StateTax(state, stateTaxes.get(state).getTaxRate()));
                } else if (i == 1) {
                    listOfProducts();
                    //String type, BigDecimal area, BigDecimal costPerSqrFt, BigDecimal laborCostPerSqrFt
                    Product product = products.get(type);
                    order.setProduct(new Product(type,order.getArea(),product.getCostPerSgrFt(),product.getLaborCost()));
                } else if (i == 3) {
                    listOfProducts();
                    //String type, BigDecimal area, BigDecimal costPerSqrFt, BigDecimal laborCostPerSqrFt
                    Product product = products.get(order.getProductType());
                    order.setProduct(new Product(order.getProductType(),area,product.getCostPerSgrFt(),product.getLaborCostPerSqrFt()));
                }
        }



        PrintWriter out;
        //clear order file to write updated order list since writeToFile only appends data
        try {
            out = new PrintWriter(new FileWriter("Orders/Orders_" + dateFormat + ".txt"));
        } catch(IOException e) {
            throw new FlooringPersistenceException("Could not update file with removed order", e);
        }
        // out.print("");
        out.flush();
        out.close();

        for(Order updatedOrder : orders.values()){
            writeOrderToFile(date,updatedOrder);
        }

        return order;
    }


    public void loadProductsAndStateTax() throws FlooringPersistenceException{
        try{
            products = readProductsFromFile(productFile);
            stateTaxes = readStateTaxFromFile(stateTaxFile);
        } catch (FlooringPersistenceException e){
            throw new FlooringPersistenceException("Could not load products and taxes", e);
        }
    }

    //grab order number, increment then overwrite
    public int nextOrderNumber () {
        int nextNum = orderNumber;
        orderNumber = orderNumber + 1;
        return nextNum;
    }
    public Map<String, StateTax> readStateTaxFromFile (String file) throws FlooringPersistenceException{
        Scanner scanner;
        Map<String, StateTax> stateTaxesFromFile = new HashMap<>();
        try{
            scanner = new Scanner(new BufferedReader(new FileReader(file)));
        } catch( FileNotFoundException e){
            throw new FlooringPersistenceException("Could not read state/tax rate file", e);
        }
        String currentLine;
        StateTax currentStateTax;
        while(scanner.hasNextLine()){
            currentLine = scanner.nextLine();
            currentStateTax = unmarshallStateTax(currentLine);
            stateTaxesFromFile.put(currentStateTax.getState(),currentStateTax);
        }
        scanner.close();
        return stateTaxesFromFile;
    }

    public Map<String, Product> readProductsFromFile (String file) throws FlooringPersistenceException{
        Scanner scanner;
        Map<String, Product> productsFromFile = new HashMap<>();
        try{
            scanner = new Scanner(new BufferedReader(new FileReader(file)));
        } catch( FileNotFoundException e){
            throw new FlooringPersistenceException("Could not read products file", e);
        }
        String currentLine;
        Product currentProduct;
        while(scanner.hasNextLine()){
            currentLine = scanner.nextLine();
            currentProduct = unmarshallProduct(currentLine);
            productsFromFile.put(currentProduct.getType(),currentProduct);
        }
        scanner.close();
        return productsFromFile;
    }


    public Map<Integer,Order> readOrdersFromFile (String file) throws FlooringPersistenceException{
        Scanner scanner;
        Map<Integer, Order> ordersFromFile = new HashMap<>();
        try{
            scanner = new Scanner(new BufferedReader(new FileReader(file)));
        } catch( FileNotFoundException e){
            throw new FlooringPersistenceException("Could not read order file", e);
        }
        String currentLine;
        Order currentOrder;
        while(scanner.hasNextLine()){
            currentLine = scanner.nextLine();
            currentOrder = unmarshallOrder(currentLine);
            ordersFromFile.put(currentOrder.getOrderNumber(),currentOrder);
        }
        scanner.close();
        return ordersFromFile;
    }
   // OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total
    public Order unmarshallOrder(String line) {
        String[] orderTokens = line.split(DEMLINITER);
        String orderNumber = orderTokens[0];
        String customerName = orderTokens[1];
        StateTax stateTaxFromFile = new StateTax(orderTokens[2], new BigDecimal(orderTokens[3]));

      //  String type, BigDecimal area, BigDecimal costPerSqrFt, BigDecimal laborCostPerSqrFt
        String type = orderTokens[4];
        BigDecimal area = new BigDecimal(orderTokens[5]);
        BigDecimal costPerSqrFt = new BigDecimal(orderTokens[6]);
        BigDecimal laborPerSqrFt = new BigDecimal(orderTokens[7]);
        Product productFromFile = new Product(type,area,costPerSqrFt,laborPerSqrFt);

        Order orderFromFile = new Order(Integer.parseInt(orderNumber),customerName,stateTaxFromFile,productFromFile);
        return orderFromFile;
    }

    public Product unmarshallProduct (String line){
        String[] productTokens = line.split(DEMLINITER);
        String type = productTokens[0];
        BigDecimal costPerSqrFt = new BigDecimal(productTokens[1]);
        BigDecimal laborPerSqrFt = new BigDecimal(productTokens[2]);

        //placeholder value for area set to 0.00;
        Product productFromFile = new Product(type,new BigDecimal("0.00"), costPerSqrFt, laborPerSqrFt);
        return productFromFile;
    }

    public StateTax unmarshallStateTax (String line){
        String[] stateTaxTokens = line.split(DEMLINITER);
        String state = stateTaxTokens[0];
        BigDecimal taxRate = new BigDecimal(stateTaxTokens[2]);
        StateTax stateTaxFromFile = new StateTax(state, taxRate);
        return stateTaxFromFile;
    }

    public List<Product> listAllProducts() throws FlooringPersistenceException{
        return new ArrayList<Product>(products.values());
    }

    public List<String> listOfProducts(){
        List<String> list = new ArrayList<String>();
        for(Product product : products.values()){
            list.add(product.getType());
        }
        return list;
    }
    public List<String> listOfStates() throws FlooringPersistenceException{
        List<String> list = new ArrayList<String>();
        for(StateTax state : stateTaxes.values()){
            list.add(state.getState());
        }
        return list;
    }

    public void loadOrderNumber() throws FlooringPersistenceException{
        Scanner scanner;
        try{
            scanner = new Scanner(new BufferedReader(new FileReader("lastordernumber.txt")));
        } catch( FileNotFoundException e){
            throw new FlooringPersistenceException("Could not read order number file", e);
        }
         orderNumber = Integer.parseInt(scanner.nextLine());
    }

    public void writeOrderNumber() throws FlooringPersistenceException{
        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter("lastordernumber.txt"));
        } catch(IOException e)
        {
            throw new FlooringPersistenceException("Could not save next order number", e);
        }

        out.println(orderNumber);
        out.flush();
        out.close();
    }

    public void exportAllData() throws FlooringPersistenceException{
        File folder = new File("Orders");
        File[] listOfFiles = folder.listFiles();
        List<String> fileDates = new ArrayList<String>();

        if(listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String date = file.getName();
                    String dateFromFile = date.substring(7, 15);
                    fileDates.add(dateFromFile);

                }
            }

            for(String date : fileDates){
                //load orders where customer name is not null
                List<Order> ordersInFile = listAllOrders(date).stream()
                        .filter((f) -> f.getCustomerName() != null)
                        .collect(Collectors.toList());
                if(!ordersInFile.isEmpty()) {
                    for (Order currentOrder : ordersInFile) {
                        exportAllDataToFile(currentOrder, date);
                    }
                }
            }
        }
    }

    public void exportAllDataToFile(Order order, String date) throws FlooringPersistenceException {
        String[] dateFormatToPrint = new String[10];
        dateFormatToPrint[0] = String.valueOf(date.charAt(0));
        dateFormatToPrint[1] = String.valueOf(date.charAt(1));
        dateFormatToPrint[2]="-";
        dateFormatToPrint[3] = String.valueOf(date.charAt(2));
        dateFormatToPrint[4] = String.valueOf(date.charAt(3));
        dateFormatToPrint[5]="-";
        dateFormatToPrint[6] = String.valueOf(date.charAt(4));
        dateFormatToPrint[7] = String.valueOf(date.charAt(5));
        dateFormatToPrint[8] = String.valueOf(date.charAt(6));
        dateFormatToPrint[9] = String.valueOf(date.charAt(7));
        String dateForExport = String.join("",dateFormatToPrint);

        PrintWriter out;
        try{
            out = new PrintWriter(new FileWriter("Backup/DataExport.txt",true));
        } catch (IOException e) {
            throw new FlooringPersistenceException("Could not backup order files", e);
        }

        String orderAsText = marshallOrder(order);
        out.println(orderAsText + "," + dateForExport);
        out.flush();
        out.close();
    }
}
