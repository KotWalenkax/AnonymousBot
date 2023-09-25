package builders;

import config.BotConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.List;

public class MessageBuilder {

    private final KeyboardBuilder keyboardBuilder;
    private final long adminId;
    private final long channelId;

    public MessageBuilder(BotConfig botConfig, KeyboardBuilder keyboardBuilder) {
            adminId = botConfig.getAdminId();
            channelId = botConfig.getChannelId();
            this.keyboardBuilder = keyboardBuilder;
    }

    public SendPhoto getMsgToAdmin(String text, List<PhotoSize> photoSizeList) {
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

    public SendPhoto getMsgToChannel(String text, List<PhotoSize> photoSizeList) {
        String preText = """
                *New question:*\s

                """;
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

    public SendMessage getMsgToChannel(String text) {
        String preText = """
                *New question:*\s

                """;
        return SendMessage
                .builder()
                .chatId(channelId)
                .text(preText + text)
                .parseMode("MarkdownV2")
                .build();
    }

    public SendMessage getSimpleMsg(long chatId, String text) {
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    public SendMessage getMsgToAdmin(String text) {
        return SendMessage
                .builder()
                .chatId(adminId)
                .text(text)
                .replyMarkup(keyboardBuilder.getAdminMenu())
                .build();
    }

    public SendMessage getAnonMsgMenu(long chatId) {
        String text = """
                *Ask a question:* 
                
                press "cancel" to stop sending message
                """;
        return SendMessage
                .builder()
                .chatId(chatId)
                .parseMode("MarkdownV2")
                .text(text)
                .replyMarkup(keyboardBuilder.getAnonymousMenu())
                .build();
    }

    public SendMessage getMainMenu(long chatId) {
        String text = "Press button \"Ask a question\" to ask a question";
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboardBuilder.getMainMenu())
                .build();
    }

    public DeleteMessage getMsgToDelete(Long chatId, Integer messageId) {
        return DeleteMessage
                .builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }

}
