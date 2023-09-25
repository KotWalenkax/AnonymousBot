package database;

import config.BotConfig;
import models.User;

import java.sql.*;

public class DBConnection {

    private static DBConnection INSTANCE;

    private static String DB_URL;
    private static String USER;
    private static String PASSWORD;

    public static DBConnection getInstance(BotConfig botConfig) {
        if (INSTANCE == null) {
            DB_URL = botConfig.getUrl();
            USER = botConfig.getUser();
            PASSWORD = botConfig.getPassword();

            INSTANCE = new DBConnection();
        }

        return INSTANCE;
    }

    private DBConnection() {
        createTable();
    }

    private Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager
                    .getConnection(DB_URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return conn;
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users" +
                "(id SERIAL PRIMARY KEY," +
                "chat_id BIGINT NOT NULL," +
                "user_status VARCHAR(25) NOT NULL)";

        try (Connection conn = connect();
             Statement statement = conn.createStatement()) {

            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(long userId) {
        User user = new User(userId);
        String sql = "SELECT user_status FROM users WHERE chat_id = ?";

        try (Connection conn = connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    user.setStatus(rs.getString("user_status"));
                } else {
                    createUser(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public void createUser(User user) {

        String sql = "INSERT INTO users(chat_id, user_status) VALUES(?, ?)";

        try (Connection conn = connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setLong(1, user.getChatId());
            statement.setString(2, user.getUserStatus().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void changeUserStatus(User user) {

        String sql = "UPDATE users SET user_status = ? WHERE chat_id = ?";

        try (Connection conn = connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, user.getUserStatus().toString());
            statement.setLong(2, user.getChatId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
