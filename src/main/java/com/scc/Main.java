package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Initialize the database before starting the application.
        if (!DatabaseInitializer.initialize()) {
            System.err.println("Application cannot start.");
            return;
        }

        UserDao userDao = new UserDao();

        try (Scanner scanner = new Scanner(System.in)) {
            Authentication auth = new Authentication(userDao, scanner);
            auth.start();
        }
    }
}
