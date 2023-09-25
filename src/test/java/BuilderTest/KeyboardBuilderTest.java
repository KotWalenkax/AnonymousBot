package BuilderTest;

import builders.KeyboardBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class KeyboardBuilderTest {

    private final KeyboardBuilder keyboardBuilder = new KeyboardBuilder();

    @Test
    public void getMainMenuTest() {
        String btnText = "Ask a question";
        String callbackData = "anonymous";

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardBuilder.getMainMenu();
        Assertions.assertNotNull(inlineKeyboardMarkup);

        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(0);

        Assertions.assertEquals(1, buttons.size());
        Assertions.assertNotNull(buttons.stream()
                .filter(btn -> btnText.equals(btn.getText())
                        && callbackData.equals(btn.getCallbackData()))
                .findAny()
                .orElse(null));

    }

    @Test
    public void getAnonymousMenuTest() {

    }

    @Test
    public void getAdminMenuTest() {
        String sendBtnText = "Send";
        String sendCallbackData = "send";

        String cancelBtnText = "Cancel";
        String cancelCallbackData = "cancel";

        InlineKeyboardMarkup inlineKeyboardMarkup = keyboardBuilder.getAdminMenu();
        Assertions.assertNotNull(inlineKeyboardMarkup);

        List<InlineKeyboardButton> buttons = inlineKeyboardMarkup.getKeyboard().get(0);

        Assertions.assertEquals(2, buttons.size());
        Assertions.assertNotNull(buttons.stream()
                .filter(btn -> sendBtnText.equals(btn.getText())
                        && sendCallbackData.equals(btn.getCallbackData()))
                .findAny()
                .orElse(null));

        Assertions.assertNotNull(buttons.stream()
                .filter(btn -> cancelBtnText.equals(btn.getText())
                        && cancelCallbackData.equals(btn.getCallbackData()))
                .findAny()
                .orElse(null));
    }

}
