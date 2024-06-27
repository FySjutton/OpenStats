package open.openstats;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.*;

import static open.openstats.openStats.LOGGER;

public class infoList extends ElementListWidget<infoList.Entry> {
    private JsonObject data;
    private LinkedHashMap<String, Boolean> view = new LinkedHashMap<>();

    private final ArrayList<String> INFO = new ArrayList<>(Arrays.asList(
            "id", "uuid", "username", "gQmynt", "onlinetime", "last_online", "last_server", "rank", "banned"
    ));

    private final ArrayList<String> SURVIVAL = new ArrayList<>(Arrays.asList(
            "survival_money", "survival_experience", "survival_plot_claims", "survival_warp_slots", "survival_quests_completed", "survival_quest_streak", "survival_level"
    ));

    private final ArrayList<String> CREATIVE = new ArrayList<>(Arrays.asList(
            "creative_rank"
    ));

    private final ArrayList<String> MB = new ArrayList<>(Arrays.asList(
            "mb_games_played", "mb_wins", "mb_winstreak", "mb_points", "mb_hat"
    ));

    private final ArrayList<String> UHC = new ArrayList<>(Arrays.asList(
            "uhc_points", "uhc_games_played", "uhc_wins", "uhc_kills", "uhc_deaths"
    ));

    private final ArrayList<String> EVENT = new ArrayList<>(Arrays.asList(
            "event_wins", "gold", "gold_earned", "mvps", "participation", "party_invites", "lobby_visibility", "random_skin", "spectator_visibility", "lobby_parkour_time", "lobby_parkour_reward", "anvil_games_played", "anvil_wins", "anvil_gold_earned", "border_runners_games_played", "border_runners_wins", "border_runners_gold_earned", "border_runners_rounds_survived", "border_runners_powerups_used", "border_runners_most_rounds_survived", "dragons_games_played", "dragons_wins", "dragons_gold_earned", "dragons_arrows_shot", "dragons_arrows_hit", "dragons_leaps_used", "dragons_crates_destroyed", "infection_games_played", "infection_wins", "infection_gold_earned", "infection_alpha_games", "infection_infected_kills", "infection_survivor_kills", "infection_most_kills_infected", "infection_most_kills_survivor", "maze_games_played", "maze_wins", "maze_gold_earned", "oitc_games_played", "oitc_wins", "oitc_gold_earned", "oitc_melee_kills", "oitc_ranged_kills", "oitc_deaths", "oitc_arrows_shot", "oitc_highest_kill_streak", "oitc_longest_bow_kill", "parkour_games_played", "parkour_wins", "parkour_gold_earned", "parkour_rounds_survived", "parkour_most_rounds_survived", "red_rover_games_played", "red_rover_wins", "red_rover_gold_earned", "red_rover_killer_games", "red_rover_rounds_survived", "red_rover_kills", "red_rover_dashes", "red_rover_most_rounds_survived", "snow_fight_games_played", "snow_fight_wins", "snow_fight_gold_earned", "snow_fight_kills", "snow_fight_snowballs_thrown", "snow_fight_snowballs_hit", "spleef_games_played", "spleef_wins", "spleef_gold_earned", "spleef_blocks_broken", "spleef_snowballs_thrown", "spleef_most_blocks_broken", "sumo_games_played", "sumo_wins", "sumo_gold_earned", "sumo_kills", "sumo_most_kills", "sg_games_played", "sg_wins", "sg_gold_earned", "sg_kills", "sg_deaths", "sg_chests_looted", "sg_most_kills", "tnt_run_games_played", "tnt_run_wins", "tnt_run_gold_earned", "tnt_run_walked_over_blocks", "tnt_run_leaps_used", "tnt_run_most_blocks_broken"
    ));

    public infoList(int width, int height, JsonObject data) {
        super(MinecraftClient.getInstance(), width, height - 20, 10, 25);
        view.put("INFO", false);
        view.put("SURVIVAL", false);
        view.put("CREATIVE", false);
        view.put("MB", false);
        view.put("UHC", false);
        view.put("EVENT", false);

        this.data = data;

        update_entries();
    }

    private void update_entries() {
        this.clearEntries();
        for (String x : view.keySet()) {
            addEntry(new Entry(true, x));
            if (view.get(x)) {
                ArrayList<String> selected = switch (x) {
                    case "INFO" -> INFO;
                    case "SURVIVAL" -> SURVIVAL;
                    case "CREATIVE" -> CREATIVE;
                    case "MB" -> MB;
                    case "UHC" -> UHC;
                    default -> EVENT;
                };

                for (String y : selected) {
                    addEntry(new Entry(false, y));
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
        private Text display;

        private TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        public Entry(boolean button, String value) {
            if (button) {
                this.button = ButtonWidget.builder(Text.of(value), btn -> toggleButton(value))
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

    private Text getText(String oriSetting, String oriValue) {
        String setting = oriSetting;
        String value = oriValue;

        if (setting.equals("id") || setting.equals("uuid")) {
            setting = setting.toUpperCase();
        } else if (!setting.equals("gQmynt")) {
            setting = WordUtils.capitalizeFully(setting.replaceAll("_", " "));
        }

        value = WordUtils.capitalizeFully(value);

        setting = setting
                .replaceAll("Tnt", "TNT")
                .replaceAll("Sg", "SG")
                .replaceAll("Oitc", "OITC")
                .replaceAll("Mvp", "MVP")
                .replaceAll("Mb", "MB")
                .replaceAll("Uhc", "UHC");

        if (oriSetting.equals("survival_money")) {
            value += " kr";
        }

        if (oriValue.isEmpty()) {
            value = "§6N/A";
        } else if (oriValue.equals("True")) {
            value = "§a" + value;
        } else if (oriValue.equals("False")) {
            value = "§c" + value;
        }

        Text textValue = Text.of(value);

        if (oriValue.equals("bashlang")) {
            MutableText finished = Text.literal("");
            finished.append(Text.literal("B").setStyle(Style.EMPTY.withColor(0x557fff)));
            finished.append(Text.literal("A").setStyle(Style.EMPTY.withColor(0x55d4ff)));
            finished.append(Text.literal("S").setStyle(Style.EMPTY.withColor(0x55ffd5)));
            finished.append(Text.literal("H").setStyle(Style.EMPTY.withColor(0x55ff80)));
            finished.append(Text.literal("L").setStyle(Style.EMPTY.withColor(0x7fff55)));
            finished.append(Text.literal("A").setStyle(Style.EMPTY.withColor(0xd4ff55)));
            finished.append(Text.literal("N").setStyle(Style.EMPTY.withColor(0xffd555)));
            finished.append(Text.literal("G").setStyle(Style.EMPTY.withColor(0xff8055)));

            textValue = finished;
        }

        return Text.of(setting + ": §7" + textValue);
    }
}
