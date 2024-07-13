package open.openstats.informationScreen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class searchField extends TextFieldWidget {
    public searchField(TextRenderer textRenderer, int width) {
        super(textRenderer, 3 * width / 8, 29, width / 4, 15, Text.of("Search Bar"));
    }
}