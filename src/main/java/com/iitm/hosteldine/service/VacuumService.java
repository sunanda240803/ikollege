package com.iitm.hosteldine.service;

import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Service
public class VacuumService {

    private final DataSource dataSource;

    public VacuumService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void vacuumDatabase() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Ensure auto-commit is enabled to run outside a transaction
            connection.setAutoCommit(true);

            // Execute the VACUUM command
            statement.execute("VACUUM");

        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
        }
    }
}

