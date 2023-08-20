package builders;

import database.DBConnection;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.List;

public class MessageBuilder {

    private static KeyboardBuilder keyboardBuilder;

    private static long adminId;
    private static long channelId;

    public MessageBuilder(long admin, long channel) {
            adminId = admin;
            channelId = channel;
            keyboardBuilder = new KeyboardBuilder();
    }

    public SendPhoto sendMsgToAdmin(String text, List<PhotoSize> photoSizeList) {
        PhotoSize photoSize = photoSizeList.get(0);
        InputFile inputFile = new InputFile(photoSize.getFileId());
        return SendPhoto
                .builder()
                .chatId(adminId)
                .photo(inputFile)
                .caption(text)
                .parseMode("MarkdownV2")
                .replyMarkup(keyboardBuilder.getAdminMenu())
                .build();

    }

    public SendPhoto sendMsgToChannel(String text, List<PhotoSize> photoSizeList) {
        String preText = "*Новый вопрос:* " + "\n\n";
        PhotoSize photoSize = photoSizeList.get(0);
        InputFile inputFile = new InputFile(photoSize.getFileId());
        return SendPhoto
                .builder()
                .chatId(channelId)
                .photo(inputFile)
                .caption(preText + text)
                .parseMode("MarkdownV2")
                .build();
    }

    public SendMessage sendMsgToChannel(String text) {
        String preText = "*Новый вопрос:* " + "\n\n";
        return SendMessage
                .builder()
                .chatId(channelId)
                .text(preText + text)
                .parseMode("MarkdownV2")
                .build();
    }

    public SendMessage sendMsg(long chatId, String text) {
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    public SendMessage sendMsgToAdmin(String text) {
        return SendMessage
                .builder()
                .chatId(adminId)
                .text(text)
                .replyMarkup(keyboardBuilder.getAdminMenu())
                .build();
    }

    public SendMessage sendAnonMsgMenu(long chatId) {
        String text = "Напиши свой вопрос: ";
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboardBuilder.getAnonymousMenu())
                .build();
    }

    public SendMessage sendMainMenu(long chatId) {
        String text = "Чтобы написать анонимный вопрос нажмите на кнопку \"Задать вопрос\"";
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboardBuilder.getMainMenuKeyboard())
                .build();
    }

}
