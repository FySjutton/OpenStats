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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                new newTab("INFO", new ArrayList<>(Arrays.asList("id", "uuid", "username", "gQmynt", "onlinetime", "last_online", "last_server", "rank", "banned"))),
                new newTab("SURVIVAL", new ArrayList<>(Arrays.asList("survival_money", "survival_experience", "survival_plot_claims", "survival_warp_slots", "survival_quests_completed", "survival_quest_streak", "survival_level"))),
                new newTab("CREATIVE", new ArrayList<>(List.of("creative_rank"))),
                new newTab("MB", new ArrayList<>(Arrays.asList("mb_games_played", "mb_wins", "mb_winstreak", "mb_points", "mb_hat"))),
                new newTab("UHC", new ArrayList<>(Arrays.asList("uhc_points", "uhc_games_played", "uhc_wins", "uhc_kills", "uhc_deaths"))),
                new newTab("EVENT", new ArrayList<>())
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
        public newTab(String tabName, ArrayList<String> info_list) {
            super(Text.of(tabName));
            GridWidget.Adder adder = grid.createAdder(1);
            if (!tabName.equals("EVENT")) {
                informationList infoList = new informationList(width, height, infoScreen.data.getAsJsonObject(), info_list);
                adder.add(infoList);
            } else {
                eventList eventlist = new eventList(width, height, infoScreen.data.getAsJsonObject());
                adder.add(eventlist);
            }
        }
    }
}
