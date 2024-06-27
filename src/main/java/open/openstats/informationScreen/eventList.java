package open.openstats.informationScreen;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import org.apache.commons.lang3.text.WordUtils;

import java.util.*;

public class eventList extends ElementListWidget<eventList.Entry> {
    private JsonObject data;
    private LinkedHashMap<String, Boolean> view = new LinkedHashMap<>();

    private enum EventType {
        INFO(new ArrayList<>(Arrays.asList(
                "event_wins", "gold", "gold_earned", "mvps", "participation", "party_invites", "lobby_visibility", "random_skin", "spectator_visibility", "lobby_parkour_time", "lobby_parkour_reward"
        ))),
        ANVIL(new ArrayList<>(Arrays.asList(
                "anvil_games_played", "anvil_wins", "anvil_gold_earned"
        ))),
        BORDER_RUNNERS(new ArrayList<>(Arrays.asList(
                "border_runners_games_played", "border_runners_wins", "border_runners_gold_earned", "border_runners_rounds_survived", "border_runners_powerups_used", "border_runners_most_rounds_survived"
        ))),
        DRAGONS(new ArrayList<>(Arrays.asList(
                "dragons_games_played", "dragons_wins", "dragons_gold_earned", "dragons_arrows_shot", "dragons_arrows_hit", "dragons_leaps_used", "dragons_crates_destroyed"
        ))),
        INFECTION(new ArrayList<>(Arrays.asList(
                "infection_games_played", "infection_wins", "infection_gold_earned", "infection_alpha_games", "infection_infected_kills", "infection_survivor_kills", "infection_most_kills_infected", "infection_most_kills_survivor"
        ))),
        MAZE(new ArrayList<>(Arrays.asList(
                "maze_games_played", "maze_wins", "maze_gold_earned"
        ))),
        OITC(new ArrayList<>(Arrays.asList(
                "oitc_games_played", "oitc_wins", "oitc_gold_earned", "oitc_melee_kills", "oitc_ranged_kills", "oitc_deaths", "oitc_arrows_shot", "oitc_highest_kill_streak", "oitc_longest_bow_kill"
        ))),
        PARKOUR(new ArrayList<>(Arrays.asList(
                "parkour_games_played", "parkour_wins", "parkour_gold_earned", "parkour_rounds_survived", "parkour_most_rounds_survived"
        ))),
        RED_ROVER(new ArrayList<>(Arrays.asList(
                "red_rover_games_played", "red_rover_wins", "red_rover_gold_earned", "red_rover_killer_games", "red_rover_rounds_survived", "red_rover_kills", "red_rover_dashes", "red_rover_most_rounds_survived"
        ))),
        SNOW_FIGHT(new ArrayList<>(Arrays.asList(
                "snow_fight_games_played", "snow_fight_wins", "snow_fight_gold_earned", "snow_fight_kills", "snow_fight_snowballs_thrown", "snow_fight_snowballs_hit"
        ))),
        SPLEEF(new ArrayList<>(Arrays.asList(
                "spleef_games_played", "spleef_wins", "spleef_gold_earned", "spleef_blocks_broken", "spleef_snowballs_thrown", "spleef_most_blocks_broken"
        ))),
        SUMO(new ArrayList<>(Arrays.asList(
                "sumo_games_played", "sumo_wins", "sumo_gold_earned", "sumo_kills", "sumo_most_kills"
        ))),
        SG(new ArrayList<>(Arrays.asList(
                "sg_games_played", "sg_wins", "sg_gold_earned", "sg_kills", "sg_deaths", "sg_chests_looted", "sg_most_kills"
        ))),
        TNT_RUN(new ArrayList<>(Arrays.asList(
                "tnt_run_games_played", "tnt_run_wins", "tnt_run_gold_earned", "tnt_run_walked_over_blocks", "tnt_run_leaps_used", "tnt_run_most_blocks_broken"
        )));

        private final ArrayList<String> stats;

        EventType(ArrayList<String> stats) {
            this.stats = stats;
        }

        public ArrayList<String> getStats() {
            return stats;
        }
    }

    public eventList(int width, int height, JsonObject data) {
        super(MinecraftClient.getInstance(), width, height - 24, 24, 25);
        for (EventType type : EventType.values()) {
            view.put(type.name(), false);
        }

        this.data = data;
        update_entries();
    }

    private void update_entries() {
        this.clearEntries();
        for (String key : view.keySet()) {
            addEntry(new Entry(true, key));
            if (view.get(key)) {
                EventType type = EventType.valueOf(key);
                for (String stat : type.getStats()) {
                    addEntry(new Entry(false, stat));
                }
            }
        }
    }

    @Override
    protected int getScrollbarX() {
        return width - 15;
    }

    @Override
    public int getRowWidth() {
        return width - 15;
    }

    public class Entry extends ElementListWidget.Entry<Entry> {
        private ButtonWidget button;
        private String display;

        private TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        public Entry(boolean button, String value) {
            if (button) {
                this.button = ButtonWidget.builder(Text.of(value.replace("_", " ")), btn -> toggleButton(value))
                    .dimensions((int) (width * 0.1), 0, (int) (width * 0.8), 20)
                    .build();
            } else {
                String text;
                try {
                    text = data.get(value).getAsString();
                } catch (Exception e) {
                    text = "";
                }
                this.display = getText(value, text);
            }
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            List<Selectable> children = new ArrayList<>();
            if (button != null) {
                children.add(button);
            }
            return children;
        }

        @Override
        public List<? extends Element> children() {
            List<Element> children = new ArrayList<>();
            if (button != null) {
                children.add(button);
            }
            return children;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (button != null) {
                button.setY(y);
                button.render(context, mouseX, mouseY, tickDelta);
            } else {
                context.drawCenteredTextWithShadow(textRenderer, display, width / 2, y + entryHeight / 2 - 9 / 2, 0xFFFFFF);
            }
        }
    }

    private void toggleButton(String btn) {
        view.replace(btn, !view.get(btn));
        update_entries();
    }

    private String getText(String setting, String value) {

        setting = WordUtils.capitalizeFully(setting.replaceAll("_", " "));

        setting = setting
                .replaceAll("Tnt", "TNT")
                .replaceAll("Sg", "SG")
                .replaceAll("Oitc", "OITC")
                .replaceAll("Mvp", "MVP")
                .replaceAll("Mb", "MB")
                .replaceAll("Uhc", "UHC");

        if (value.isEmpty()) {
            value = "§6N/A";
        }

        return setting + ": §7" + value;
    }
}