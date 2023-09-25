package builders;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {

    //start menu keyboard
    public InlineKeyboardMarkup getMainMenu() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton anonMsgBtn = new InlineKeyboardButton("Ask a question");

        anonMsgBtn.setCallbackData("anonymous");
        buttons.add(anonMsgBtn);
        keyboard.add(buttons);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    //keyboard with one button "cancel" to stop sending anon msg
    public InlineKeyboardMarkup getAnonymousMenu() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton cancel = new InlineKeyboardButton("Cancel");

        cancel.setCallbackData("cancel");
        buttons.add(cancel);
        keyboard.add(buttons);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    //admin keyboard with btn "send" and "cancel" respectively to send or not the msg
    public InlineKeyboardMarkup getAdminMenu() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton send = new InlineKeyboardButton("Send");
        InlineKeyboardButton cancel = new InlineKeyboardButton("Cancel");

        send.setCallbackData("send");
        cancel.setCallbackData("cancel");

        buttons.add(send);
        buttons.add(cancel);
        keyboard.add(buttons);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

}
