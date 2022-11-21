package com.sg.flooring.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Product {
    private String type;
    private BigDecimal area;
    private BigDecimal costPerSqrFt;
    private BigDecimal laborCostPerSqrFt;

    public Product(String type, BigDecimal area, BigDecimal costPerSqrFt, BigDecimal laborCostPerSqrFt){
        this.type = type;
        this.area = area;
        this.costPerSqrFt = costPerSqrFt.setScale(2, RoundingMode.FLOOR);
        this.laborCostPerSqrFt = laborCostPerSqrFt.setScale(2, RoundingMode.FLOOR);
    }
    public String getType() {
        return type;
    }

    public BigDecimal getArea() {
        return area;
    }

    public BigDecimal getCostPerSgrFt() {
        return costPerSqrFt.setScale(2, RoundingMode.FLOOR);
    }

    public BigDecimal getLaborCostPerSqrFt() {
        return laborCostPerSqrFt.setScale(2, RoundingMode.FLOOR);
    }

    public BigDecimal getLaborCost() {
        return area.multiply(laborCostPerSqrFt).setScale(2, RoundingMode.FLOOR);
    }


    public void setType(String type){
        this.type = type;
    }
    public void setArea(BigDecimal area){
        this.area = area;
    }
    public void setCostPerSqrFt (BigDecimal costPerSqrFt){
        this.costPerSqrFt = costPerSqrFt;
    }
    public void setLaborCostPerSqrFt(BigDecimal laborCostPerSqrFt){
        this.laborCostPerSqrFt = laborCostPerSqrFt;
    }


}
