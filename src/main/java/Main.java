import bot.AnonBot;
import builders.KeyboardBuilder;
import builders.MessageBuilder;
import config.BotConfig;
import database.DBConnection;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import service.UserServiceImpl;

public class Main {

    public static void main(String[] args) {

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
            BotConfig botConfig = new BotConfig();

            MessageBuilder messageBuilder = new MessageBuilder(botConfig, keyboardBuilder);

            UserServiceImpl userService = new UserServiceImpl(DBConnection.getInstance(botConfig));

            AnonBot anonBot = new AnonBot(botConfig, messageBuilder, userService);

            botsApi.registerBot(anonBot);
        } catch(TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
