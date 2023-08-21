package service;

import models.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserService {
    User getUserById(Update update);
    void changeStatus(User user, String status);
}
