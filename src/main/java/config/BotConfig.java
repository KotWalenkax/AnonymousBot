package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfig {

    private String botToken;
    private String botName;

    private long adminId;
    private long channelId;

    private String user;
    private String password;
    private String url;

    public BotConfig() {
        loadSettings();
    }

    private void loadSettings() {
        Properties botProps = new Properties();
        try (InputStream rootPath = BotConfig.class.getClassLoader().getResourceAsStream("config.properties");) {
            botProps.load(rootPath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        botToken = botProps.getProperty("bot.token");
        botName = botProps.getProperty("bot.name");

        adminId = Long.parseLong(botProps.getProperty("admin.id"));
        channelId = Long.parseLong(botProps.getProperty("channel.id"));

        user = botProps.getProperty("db.user");
        password = botProps.getProperty("db.password");
        url = botProps.getProperty("db.url");
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotName() {
        return botName;
    }

    public Long getAdminId() {
        return adminId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

}
