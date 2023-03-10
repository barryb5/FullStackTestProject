package com.company;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DatabaseManager {
    public Connection db;
    private String url = "jdbc:sqlite:";
    DatabaseManager() {
        try {
            db = DriverManager.getConnection("jdbc:sqlite:test.db");
            resetDatabase();
            addChat("test1");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public void resetDatabase() {
        try {
            Statement statement = db.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.execute("PRAGMA foreign_keys = 0");

            statement.execute("DROP TABLE IF EXISTS chats");
            statement.execute("DROP TABLE IF EXISTS messages");

            statement.execute("CREATE TABLE chats (id TEXT ONLY PRIMARY KEY)");

            statement.execute("CREATE TABLE messages (id INTEGER IDENTITY PRIMARY KEY, chatID TEXT ONLY NOT NULL, message TEXT ONLY, date TEXT ONLY, FOREIGN KEY(chatID) REFERENCES chats(id))");

            statement.execute("PRAGMA foreign_keys = 1");

        } catch (SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void addMessage(String chatID, String date, String message) {
        try {
            Statement statement = db.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate(String.format("INSERT INTO messages VALUES('%s', '%s', '%s')", chatID, date, message));
        } catch (SQLException e) {
            System.out.println("Insert chat error");
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void addChat(String chatID) {
        try {
            Statement statement = db.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate(String.format("INSERT INTO chats VALUES('%s')", chatID));
        } catch (SQLException e) {
            System.out.println("Insert chat error");
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public ResultSet getChats() {
        try {
            Statement statement = db.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            return statement.executeQuery("SELECT * FROM chats");
        } catch (SQLException e) {
            System.out.println("Get Chats Error");
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }

    public ResultSet getMessages(String chatID) {
        try {
            Statement statement = db.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            return statement.executeQuery(String.format("SELECT * FROM messages WHERE chatID = '%s'", chatID));
        } catch (SQLException e) {
            System.out.println("Get Chats Error");
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return null;
    }
}
