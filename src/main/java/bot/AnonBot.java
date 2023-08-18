package bot;

import builders.KeyboardBuilder;
import config.BotConfig;
import database.DBConnection;
import models.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class AnonBot extends TelegramLongPollingBot {

    private final KeyboardBuilder keyboardBuilder;
    private final DBConnection dbConnection;

    private final long adminId;
    private final long channelId;
    private final String botName;

    public AnonBot(BotConfig botConfig) {
        super(botConfig.getBotToken());

        adminId = botConfig.getAdminId();
        channelId = botConfig.getChannelId();
        botName = botConfig.getBotName();

        keyboardBuilder = KeyboardBuilder.getInstance();
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
                    sendAnonMsgMenu(user.getChatId());
                    break;
            }
        }

        if (update.hasCallbackQuery()) {
            String mode = update.getCallbackQuery().getData();

            switch (mode) {
                case "anonymous" -> {
                    user.setUserStatus("ANONYMOUS");
                    dbConnection.changeUserStatus(user);
                    sendAnonMsgMenu(user.getChatId());
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
                        sendMsgToChannel(msg, update.getCallbackQuery().getMessage().getPhoto());
                    } else {
                        sendMsgToChannel(msg);
                    }
                    sendMsg(user.getChatId(), "Сообщение отправлено");
                    sendMainMenu(user.getChatId());
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
                sendAnonMsgMenu(user.getChatId());
            } else {

                if (update.getMessage().hasPhoto()) {
                    text = update.getMessage().getCaption();
                    sendMsgToAdmin(text, update.getMessage().getPhoto());
                } else {
                    sendMsgToAdmin(text);
                }
                sendMsg(user.getChatId(), "Сообщение отправлено");
                sendMainMenu(user.getChatId());
            }
        }

        if (update.hasCallbackQuery()) {
            String mode = update.getCallbackQuery().getData();

            switch (mode) {
                case "cancel":
                    user.setUserStatus("DEFAULT");
                    dbConnection.changeUserStatus(user);
                    sendMainMenu(user.getChatId());
                    break;
            }
        }

    }

    private void sendMsgToAdmin(String text, List<PhotoSize> photoSizeList) {
        PhotoSize photoSize = photoSizeList.get(0);
        InputFile inputFile = new InputFile(photoSize.getFileId());
        sendMessage(SendPhoto
                .builder()
                .chatId(adminId)
                .photo(inputFile)
                .caption(text)
                .parseMode("MarkdownV2")
                .replyMarkup(keyboardBuilder.getAdminMenu())
                .build());

    }

    private void sendMsgToChannel(String text, List<PhotoSize> photoSizeList) {
        String preText = "*Новый вопрос:* " + "\n\n";
        PhotoSize photoSize = photoSizeList.get(0);
        InputFile inputFile = new InputFile(photoSize.getFileId());
        sendMessage(SendPhoto
                .builder()
                .chatId(channelId)
                .photo(inputFile)
                .caption(preText + text)
                .parseMode("MarkdownV2")
                .build());
    }

    private void sendMsgToChannel(String text) {
        String preText = "*Новый вопрос:* " + "\n\n";
        sendMessage(SendMessage
                .builder()
                .chatId(channelId)
                .text(preText + text)
                .parseMode("MarkdownV2")
                .build());
    }

    private void sendMsg(long chatId, String text) {
        sendMessage(SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build());
    }

    private void sendMsgToAdmin(String text) {
        sendMessage(SendMessage
                .builder()
                .chatId(adminId)
                .text(text)
                .replyMarkup(keyboardBuilder.getAdminMenu())
                .build());
    }

    private void sendAnonMsgMenu(long chatId) {
        String text = "Напиши свой вопрос: ";
        sendMessage(SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboardBuilder.getAnonymousMenu())
                .build());
    }

    private void sendMainMenu(long chatId) {
        String text = "Чтобы написать анонимный вопрос нажмите на кнопку \"Задать вопрос\"";
        sendMessage(SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboardBuilder.getMainMenuKeyboard())
                .build());
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
