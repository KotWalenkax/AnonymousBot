package ServiceTest;

import database.DBConnection;
import models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import service.UserServiceImpl;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private DBConnection dbConnection;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void getUserByIdWithMessageTest() {
        Update update = getUpdateWithMessage();
        User user = new User(1L);

        given(dbConnection.getUser(anyLong())).willReturn(user);

        User testUser = userService.getUserById(update);

        Assertions.assertNotNull(testUser);
        Assertions.assertEquals(user, testUser);
    }

    @Test
    public void getUserByIdWithCallbackQueryTest() {
        Update update = getUpdateWithCallbackQuery();
        User user = new User(1L);

        given(dbConnection.getUser(anyLong())).willReturn(user);

        User testUser = userService.getUserById(update);

        Assertions.assertNotNull(testUser);
        Assertions.assertEquals(user, testUser);
    }

    private Update getUpdateWithMessage() {
        Chat chat = new Chat();
        chat.setId(1L);

        Message message = new Message();
        message.setChat(chat);

        Update update = new Update();
        update.setMessage(message);

        return update;
    }

    private Update getUpdateWithCallbackQuery() {
        Chat chat = new Chat();
        chat.setId(1L);

        Message message = new Message();
        message.setChat(chat);

        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setMessage(message);

        Update update = new Update();
        update.setCallbackQuery(callbackQuery);

        return update;
    }
}
