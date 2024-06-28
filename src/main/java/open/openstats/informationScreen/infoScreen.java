package open.openstats.informationScreen;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.text.Text;

public class infoScreen extends Screen {
    private static JsonElement data;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);

    public infoScreen(JsonElement data) {
        super(Text.of("OpenStats"));
        infoScreen.data = data;
    }

    @Override
    public void init() {
        TabNavigationWidget tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width).tabs(new Tab[]{
                new newTab("INFO"),
                new newTab("SURVIVAL"),
                new newTab("CREATIVE"),
                new newTab("MB"),
                new newTab("UHC"),
                new newTab("EVENT")
        }).build();
        this.addDrawableChild(tabNavigation);

        tabNavigation.selectTab(0, false);
        tabNavigation.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    private class newTab extends GridScreenTab {
        public newTab(String tabName) {
            super(Text.of(tabName));
            GridWidget.Adder adder = grid.createAdder(1);
            if (!tabName.equals("EVENT")) {
                informationList infoList = new informationList(width, height, tabName, infoScreen.data.getAsJsonObject());
                adder.add(infoList);
            } else {
                eventList eventlist = new eventList(width, height, infoScreen.data.getAsJsonObject());
                adder.add(eventlist);
            }
        }
    }
}
