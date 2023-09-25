package service;

import database.DBConnection;
import models.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UserServiceImpl implements UserService{

    private final DBConnection dbConnection;

    public UserServiceImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public User getUserById(Update update) {
        User user;
        if (update.hasMessage()) {
            user = dbConnection.getUser(update.getMessage().getChatId());
        } else {
            user = dbConnection.getUser(update.getCallbackQuery().getMessage().getChatId());
        }

        return user;
    }

    @Override
    public void changeStatus(User user) {
        dbConnection.changeUserStatus(user);
    }
}
