package bot;

import builders.MessageBuilder;
import config.BotConfig;
import database.DBConnection;
import models.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class AnonBot extends TelegramLongPollingBot {

    private final DBConnection dbConnection;
    private final MessageBuilder messageBuilder;

    private final String botName;

    public AnonBot(BotConfig botConfig) {
        super(botConfig.getBotToken());

        botName = botConfig.getBotName();

        messageBuilder = new MessageBuilder(botConfig.getAdminId(), botConfig.getChannelId());
        dbConnection = DBConnection.getInstance(botConfig.getUrl(), botConfig.getUser(), botConfig.getPassword());
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        User user;

        if (update.hasMessage()) {
            user = dbConnection.getUser(update.getMessage().getChatId());
        } else {
            user = dbConnection.getUser(update.getCallbackQuery().getMessage().getChatId());
        }

        switch (user.getUserStatus()) {
            case DEFAULT -> defaultStatus(update, user);
            case ANONYMOUS -> anonymousStatus(update, user);
        }

    }

    private void defaultStatus(Update update, User user){

        if (update.hasMessage()) {
            String mode = update.getMessage().getText();

            switch (mode) {
                case "/start":
                    user.setUserStatus("ANONYMOUS");
                    dbConnection.changeUserStatus(user);
                    sendMessage(messageBuilder.sendMainMenu(user.getChatId()));
                    break;
            }
        }

        if (update.hasCallbackQuery()) {
            String mode = update.getCallbackQuery().getData();

            switch (mode) {
                case "anonymous" -> {
                    user.setUserStatus("ANONYMOUS");
                    dbConnection.changeUserStatus(user);
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
            user.setUserStatus("DEFAULT");
            dbConnection.changeUserStatus(user);
            String text = "";

            if (update.getMessage().hasText()) {
                text = update.getMessage().getText();
            }

            if (text.contains("/start")) {
                user.setUserStatus("ANONYMOUS");
                dbConnection.changeUserStatus(user);
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
                case "cancel":
                    user.setUserStatus("DEFAULT");
                    dbConnection.changeUserStatus(user);
                    sendMessage(messageBuilder.sendMainMenu(user.getChatId()));
                    break;
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
