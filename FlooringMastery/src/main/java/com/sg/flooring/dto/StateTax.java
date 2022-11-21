package com.sg.flooring.dto;

import java.math.BigDecimal;

public class StateTax {

    private String state;
    private BigDecimal taxRate;

    public StateTax(String state, BigDecimal taxRate){
        this.state = state;
        this.taxRate = taxRate;
    }
    public String getState() {
        return state;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setStateAbb(String stateAbb){
        this.state = state;
    }
    public void setTaxRate(BigDecimal taxRate){
        this.taxRate = taxRate;
    }
}
