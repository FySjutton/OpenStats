package open.openstats;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import static open.openstats.openStats.LOGGER;

public class infoScreen extends Screen {
    private JsonElement data;

    public infoScreen(JsonElement data) {
        super(Text.of("OpenStats"));
        this.data = data;
    }

    @Override
    public void init() {
//        int y = 2;
//        int buttWidth = width / 6;
//        ButtonWidget info = ButtonWidget.builder(Text.literal("Info"), btn -> {})
//            .dimensions(0, y, buttWidth, 20)
//            .build();
//        addDrawableChild(info);
//        ButtonWidget survival = ButtonWidget.builder(Text.literal("Survival"), btn -> {})
//                .dimensions(buttWidth, y, buttWidth, 20)
//                .build();
//        addDrawableChild(survival);
//        ButtonWidget creative = ButtonWidget.builder(Text.literal("Creative"), btn -> {})
//                .dimensions(buttWidth * 2, y, buttWidth, 20)
//                .build();
//        addDrawableChild(creative);
//        ButtonWidget mb = ButtonWidget.builder(Text.literal("MB"), btn -> {})
//                .dimensions(buttWidth * 3, y, buttWidth, 20)
//                .build();
//        addDrawableChild(mb);
//        ButtonWidget uhc = ButtonWidget.builder(Text.literal("UHC"), btn -> {})
//                .dimensions(buttWidth * 4, y, buttWidth, 20)
//                .build();
//        addDrawableChild(uhc);
//        ButtonWidget event = ButtonWidget.builder(Text.literal("Event"), btn -> {})
//                .dimensions(buttWidth * 5, y, width - buttWidth * 5, 20)
//                .build();
//        addDrawableChild(event);

        infoList info = new infoList(width, height, data.getAsJsonObject());
        addDrawableChild(info);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        renderDarkening(context);
//        renderBackground(context, mouseX, mouseY, delta);
//        context.fill(0, 0, width, 24, 0xFF383838);
        super.render(context, mouseX, mouseY, delta);
//        for (Element widget : this.children()) {
//            ((ButtonWidget) widget).render(context, mouseX, mouseY, delta);
//        }
    }
}
