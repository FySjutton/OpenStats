package open.openstats.informationScreen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.Timer;
import java.util.TimerTask;

import static open.openstats.openStats.LOGGER;

public class searchField extends TextFieldWidget {
    private Timer timer;
    private static final long DELAY = 800;

    public searchField(TextRenderer textRenderer, int width, infoScreen parent) {
        super(textRenderer, width / 2 - width / 6, 29, width / 3, 15, Text.of("Search Bar"));
        super.setChangedListener(text -> {
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    parent.search(text);
                    timer.cancel();
                }
            }, DELAY);
        });
    }
}