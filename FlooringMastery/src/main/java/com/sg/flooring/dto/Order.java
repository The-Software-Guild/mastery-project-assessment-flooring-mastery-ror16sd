package com.sg.flooring.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Order {
    private int orderNumber;
    private String customerName;

    //State and Corresponding Tax Rate
    private StateTax stateTax;
    //contains productType, Area, costPerSqrft, laborCostPerSqrFt, laborCosts, materialCosts
    private Product product;

    //can't add orderNumber. That is assigned based off the other orders that have been processed
    //can't set tax or total. Those are derived from the contents of a Product obj which
    //will be accessed then computed with getters
    public Order(int orderNumber,String customerName, StateTax state,Product product){
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.stateTax = state;
        this.product = product;
    }

    //Mutators - Setters
    public void setCustomerName(String name){
        this.customerName = name;
    }
    public void setStateTax(StateTax stateTax){
        this.stateTax = stateTax;
    }
    public void setProduct(Product product){
        this.product = product;
    }

    //REMOVE? Depending upon how orderNumber as an ID get implmented
    public void setOrderNumber(int orderNumber){
        this.orderNumber = orderNumber;
    }


    //Accessors - Getters
    public int getOrderNumber(){
        return orderNumber;
    }
    public String getCustomerName(){
        return customerName;
    }
    public String getState(){
        return stateTax.getState();
    }
    public BigDecimal getTaxRate(){
        return stateTax.getTaxRate();
    }
    public String getProductType() {
        return product.getType();
    }
    public BigDecimal getArea(){
        return product.getArea();
    }
    public BigDecimal getCostPerSqrFt(){
        return product.getCostPerSgrFt();
    }
    public BigDecimal getLaborCostPerSqrFt(){
        return product.getLaborCostPerSqrFt();
    }
    public BigDecimal getLaborCost(){
        return product.getLaborCost();
    }
    public BigDecimal getMaterialCost(){
        BigDecimal area = getArea();
        BigDecimal costPerSqrFt = getCostPerSqrFt();
        return area.multiply(costPerSqrFt).setScale(2, RoundingMode.FLOOR);
    }

    public BigDecimal getTax(){
        BigDecimal material = getMaterialCost();
        BigDecimal labor = getLaborCost();
        BigDecimal taxRate = getTaxRate();
        BigDecimal tax1 = material.add(labor);
        BigDecimal tax2 = taxRate.divide(new BigDecimal(100),RoundingMode.FLOOR).setScale(2,RoundingMode.FLOOR);
        return tax1.multiply(tax2).setScale(2,RoundingMode.FLOOR);
    }

    public BigDecimal getTotal(){
        BigDecimal material = getMaterialCost();
        BigDecimal labor = getLaborCost();
        BigDecimal tax = getTax();
        return material.add(labor).add(tax).setScale(2, RoundingMode.FLOOR);
    }
}
