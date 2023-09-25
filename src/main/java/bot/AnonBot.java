package bot;

import builders.MessageBuilder;
import config.BotConfig;
import models.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.UserService;
import service.UserServiceImpl;

public class AnonBot extends TelegramLongPollingBot {

    private final UserService userService;
    private final MessageBuilder messageBuilder;

    private final String botName;
    private final Long adminId;

    public AnonBot(BotConfig botConfig, MessageBuilder messageBuilder, UserServiceImpl userService) {
        super(botConfig.getBotToken());
        botName = botConfig.getBotName();
        adminId = botConfig.getAdminId();

        this.messageBuilder = messageBuilder;
        this.userService = userService;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        User user = userService.getUserById(update);
        System.out.println(update.hasMessage() && update.getMessage().hasPhoto());

        if (user.getChatId() == adminId) {
            System.out.println("User is admin");
            handleAdminMessage(update);

        } else if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().contains("/")) {
                System.out.println("Message contains /");
                handleCommand(update, user);
            }

        } else {
            System.out.println(user.getUserStatus());
            switch (user.getUserStatus()) {
                case DEFAULT -> handleDefaultStatus(update, user);
                case ANONYMOUS -> handleAnonymousStatus(update, user);
            }
        }
    }

    public void handleAdminMessage(Update update) {
        if (update.hasCallbackQuery()) {
          CallbackQuery cbQuery = update.getCallbackQuery();
            Message message = cbQuery.getMessage();

          switch (cbQuery.getData()) {
              case "cancel" -> deleteMessage(messageBuilder.getMsgToDelete(message.getChatId(), message.getMessageId()));
              case "send" -> {
                  String msgText;

                  if (message.hasPhoto()) {
                      msgText = message.getCaption() == null ? "" : message.getCaption();
                      sendMessage(messageBuilder.getMsgToChannel(msgText, message.getPhoto()));
                  } else {
                      msgText = message.hasText() ? message.getText() : "";
                      sendMessage(messageBuilder.getMsgToChannel(msgText));
                  }

                  deleteMessage(messageBuilder.getMsgToDelete(message.getChatId(), message.getMessageId()));
              }
          }
        }
    }

    public void handleDefaultStatus(Update update, User user){
        System.out.println("DEFAULT status");
        if (update.hasCallbackQuery()) {
            String mode = update.getCallbackQuery().getData();

            switch (mode) {
                case "anonymous" -> {
                    changeStatus(user, "ANONYMOUS");
                    sendMessage(messageBuilder.getAnonMsgMenu(user.getChatId()));
                    System.out.println("anon menu msg sent");
                }
            }
        }
    }

    public void handleAnonymousStatus(Update update, User user){
        System.out.println("ANONYMOUS STATUS");
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String msgText;

            if (message.hasPhoto()) {
                System.out.println("Photo exists");
                msgText = message.getCaption() == null ? "" : message.getCaption();
                System.out.println(msgText);
                sendMessage(messageBuilder.getMsgToAdmin(msgText, message.getPhoto()));
            } else {
                System.out.println("Photo doesnt exist");
                msgText = message.hasText() ? message.getText() : "";
                System.out.println(msgText);
                sendMessage(messageBuilder.getMsgToAdmin(msgText));
            }

            changeStatus(user, "DEFAULT");
            sendMessage(messageBuilder.getSimpleMsg(user.getChatId(), "Message sent"));
            sendMessage(messageBuilder.getMainMenu(user.getChatId()));
            }

        if (update.hasCallbackQuery()) {
            CallbackQuery cbQuery = update.getCallbackQuery();

            String cbQueryData = cbQuery.getData();

            switch (cbQueryData) {
                case "cancel" -> {
                    changeStatus(user, "DEFAULT");
                    sendMessage(messageBuilder.getMainMenu(user.getChatId()));
                }
            }
        }
    }

    public void handleCommand(Update update, User user) {
        Message message = update.getMessage();

        if (message.getText().contains("/start")) {
            changeStatus(user, "ANONYMOUS");
            sendMessage(messageBuilder.getAnonMsgMenu(user.getChatId()));
            System.out.println("anon menu sent");
        }
    }

    public void changeStatus(User user, String newStatus) {
        user.setStatus(newStatus);
        userService.changeStatus(user);
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(DeleteMessage deleteMessage) {
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
