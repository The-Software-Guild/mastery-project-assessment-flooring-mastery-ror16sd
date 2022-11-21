package com.sg.flooring.dao;

import com.sg.flooring.service.FlooringPersistenceException;

public interface FlooringAuditDao {
    public void writeAuditEntry(String entry) throws FlooringPersistenceException;
}
