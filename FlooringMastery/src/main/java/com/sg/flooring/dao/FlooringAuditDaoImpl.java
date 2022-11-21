package com.sg.flooring.dao;

import com.sg.flooring.service.FlooringPersistenceException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class FlooringAuditDaoImpl implements FlooringAuditDao{
    public static final String AUDIT_FILE = "audit.txt";

    @Override
    public void writeAuditEntry(String entry) throws FlooringPersistenceException {
        PrintWriter out;
        try{
            out = new PrintWriter(new FileWriter(AUDIT_FILE, true));
        } catch (IOException e){
            throw new FlooringPersistenceException("Could not persist audit information.", e);
        }
        LocalDateTime timeStamp = LocalDateTime.now();
        out.println(timeStamp.toString() + " : " + entry);
        out.flush();
    }
}