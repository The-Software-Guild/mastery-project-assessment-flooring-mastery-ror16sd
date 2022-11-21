package com.sg.flooring.service;

public class FlooringPersistenceException extends Exception{
    public FlooringPersistenceException(String msg){super(msg);}

    public FlooringPersistenceException(String msg, Throwable cause){super(msg,cause);}
}
