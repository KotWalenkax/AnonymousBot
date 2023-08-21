package bot;

import builders.MessageBuilder;
import config.BotConfig;
import models.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.UserService;
import service.UserServiceImpl;

public class AnonBot extends TelegramLongPollingBot {

    private final UserService userService;
    private final MessageBuilder messageBuilder;

    private final String botName;

    public AnonBot(BotConfig botConfig) {
        super(botConfig.getBotToken());
        botName = botConfig.getBotName();

        messageBuilder = new MessageBuilder(botConfig.getAdminId(), botConfig.getChannelId());
        userService = new UserServiceImpl(botConfig.getUrl(), botConfig.getUser(), botConfig.getPassword());
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        User user = userService.getUserById(update);

        switch (user.getUserStatus()) {
            case DEFAULT -> defaultStatus(update, user);
            case ANONYMOUS -> anonymousStatus(update, user);
        }
    }

    private void defaultStatus(Update update, User user){

        if (update.hasMessage()) {
            String mode = update.getMessage().getText();
            switch (mode) {
                case "/start" -> {
                    userService.changeStatus(user, "ANONYMOUS");
                    sendMessage(messageBuilder.sendMainMenu(user.getChatId()));
                }
            }
        }

        if (update.hasCallbackQuery()) {
            String mode = update.getCallbackQuery().getData();

            switch (mode) {
                case "anonymous" -> {
                    userService.changeStatus(user,  "ANONYMOUS");
                    sendMessage(messageBuilder.sendAnonMsgMenu(user.getChatId()));
                }
                case "cancel" -> {
                    //delete msg, or save, but what is the purpose of saving message, that u don`t send?
                }
                case "send" -> {
                    String msg = "";
                    if (update.getCallbackQuery().getMessage().hasText()) {
                        msg = update.getCallbackQuery().getMessage().getText();
                    }
                    if (update.getCallbackQuery().getMessage().hasPhoto()) {
                        msg = update.getCallbackQuery().getMessage().getCaption();
                        sendMessage(messageBuilder.sendMsgToChannel(msg, update.getCallbackQuery().getMessage().getPhoto()));
                    } else {
                        sendMessage(messageBuilder.sendMsgToChannel(msg));
                    }
                    sendMessage(messageBuilder.sendMsg(user.getChatId(), "Сообщение отправлено"));
                    sendMessage(messageBuilder.sendMainMenu(user.getChatId()));
                }
            }
        }

    }

    private void anonymousStatus(Update update, User user){

        if (update.hasMessage()) {
            userService.changeStatus(user,  "DEFAULT");
            String text = "";

            if (update.getMessage().hasText()) {
                text = update.getMessage().getText();
            }

            if (text.contains("/start")) {
                userService.changeStatus(user,  "ANONYMOUS");
                sendMessage(messageBuilder.sendAnonMsgMenu(user.getChatId()));
            } else {

                if (update.getMessage().hasPhoto()) {
                    text = update.getMessage().getCaption();
                    sendMessage(messageBuilder.sendMsgToAdmin(text, update.getMessage().getPhoto()));
                } else {
                    sendMessage(messageBuilder.sendMsgToAdmin(text));
                }
                sendMessage(messageBuilder.sendMsg(user.getChatId(), "Сообщение отправлено"));
                sendMessage(messageBuilder.sendMainMenu(user.getChatId()));
            }
        }

        if (update.hasCallbackQuery()) {
            String mode = update.getCallbackQuery().getData();

            switch (mode) {
                case "cancel" -> {
                    userService.changeStatus(user, "DEFAULT");
                    sendMessage(messageBuilder.sendMainMenu(user.getChatId()));
                }
            }
        }

    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
