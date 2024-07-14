package open.openstats.informationScreen;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static open.openstats.openStats.LOGGER;

public class infoScreen extends Screen {
    private static JsonElement data;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private final Identifier SEARCH_ICON = Identifier.ofVanilla("icon/search");
    private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> informationLines = infoLines.informationLines;

    public infoScreen(JsonElement data) {
        super(Text.of("OpenStats"));
        infoScreen.data = data;
    }

    @Override
    public void init() {
        Tab[] tabs = new Tab[informationLines.size()];
        int index = 0;
        for (String x : informationLines.keySet()) {
            tabs[index] = new newTab(x.toUpperCase(), informationLines.get(x));
            index++;
        }

        TabNavigationWidget tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width).tabs(tabs).build();
        this.addDrawableChild(tabNavigation);

        searchField searchbar = new searchField(textRenderer, width, this);
        this.addDrawableChild(searchbar);

        tabNavigation.selectTab(0, false);
        tabNavigation.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawGuiTexture(SEARCH_ICON, width / 2 - width / 6 - 14, 31, 12, 12);
    }

    private class newTab extends GridScreenTab {
        public informationList infoList;
        public newTab(String tabName, LinkedHashMap<String, ArrayList<String>> info_list) {
            super(Text.of(tabName));
            GridWidget.Adder adder = grid.createAdder(1);

            infoList = new informationList(width, height, infoScreen.data.getAsJsonObject(), info_list);
            adder.add(infoList);
        }
    }

    public void search(String text) {
        LOGGER.info("Searching for \"" + text + "\"...");
        LinkedHashMap<String, ArrayList<String>> searchResults = new LinkedHashMap<>();

        ((newTab) this.tabManager.getCurrentTab()).infoList.updateViewList(searchResults);
    }
}
