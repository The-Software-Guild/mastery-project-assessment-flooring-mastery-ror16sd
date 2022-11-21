package com.sg.flooring.service;

public class FlooringDataValidationException extends Exception{

    public FlooringDataValidationException(String msg){super(msg);}

    public FlooringDataValidationException(String msg, Throwable cause){super(msg,cause);}
}
