package builders;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {

    private static final KeyboardBuilder INSTANCE = new KeyboardBuilder();

    public static KeyboardBuilder getInstance() {
        return INSTANCE;
    }

    //start menu keyboard
    public InlineKeyboardMarkup getMainMenuKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton anonMsgBtn = new InlineKeyboardButton("Задать вопрос");
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
        InlineKeyboardButton cancel = new InlineKeyboardButton("Отменить");
        cancel.setCallbackData("cancel");
        buttons.add(cancel);
        keyboard.add(buttons);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    //admin keyboard, where he decides send or not the msg
    public InlineKeyboardMarkup getAdminMenu() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton send = new InlineKeyboardButton("Отправить?");
        InlineKeyboardButton cancel = new InlineKeyboardButton("Лол каво");
        send.setCallbackData("send");
        cancel.setCallbackData("cancel");
        buttons.add(send);
        buttons.add(cancel);
        keyboard.add(buttons);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

}
