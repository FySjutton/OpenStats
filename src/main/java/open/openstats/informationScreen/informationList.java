package open.openstats.informationScreen;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import org.apache.commons.lang3.text.WordUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class informationList extends ElementListWidget<informationList.Entry> {
    private JsonObject data;

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

    public informationList(int width, int height, String tabName, JsonObject data) {
        super(MinecraftClient.getInstance(), width, height - 24, 24, 25);

        this.data = data;

        ArrayList<String> selected = switch (tabName) {
            case "INFO" -> INFO;
            case "SURVIVAL" -> SURVIVAL;
            case "CREATIVE" -> CREATIVE;
            case "MB" -> MB;
            case "UHC" -> UHC;
            default -> EVENT;
        };

        for (String x : selected) {
            addEntry(new Entry(x));
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
        private final String displayText;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        public Entry(String setting) {
            String value;
            try {
                value = data.get(setting).getAsString();
            } catch (Exception e) {
                value = "";
            }
            this.displayText = getText(setting, value);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawCenteredTextWithShadow(textRenderer, displayText, width / 2, y + entryHeight / 2 - 9 / 2, 0xFFFFFF);
        }
    }

    private String getText(String oriSetting, String oriValue) {
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

        switch (oriSetting) {
            case "survival_money" -> value += " kr";
            case "survival_experience" -> value += " XP";
            case "onlinetime" -> value = parseMillis(Long.parseLong(value));
            case "creative_rank" -> value = parseCreativeRank(oriValue);
        }

        if (oriValue.isEmpty()) {
            value = "§6N/A";
        } else if (oriValue.equals("True")) {
            value = "§a" + value;
        } else if (oriValue.equals("False")) {
            value = "§c" + value;
        }

        return setting + ": §7" + value;
    }

    public static String parseMillis(long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long months = days / 30; // May not be true as not all months are 30 days, but yeah...

        days %= 30;
        hours %= 24;
        minutes %= 60;
        seconds %= 60;

        StringBuilder sb = new StringBuilder();

        if (months > 0) {sb.append(months).append(" mån, ");}
        if (days > 0) {sb.append(days).append(" d, ");}
        if (hours > 0) {sb.append(hours).append(" h, ");}
        if (minutes > 0) {sb.append(minutes).append(" min, ");}
        if (seconds > 0) {sb.append(seconds).append(" sek");}

        if (!sb.isEmpty() && sb.charAt(sb.length() - 2) == ',') {
            sb.delete(sb.length() - 2, sb.length());
        }

        return sb.toString();
    }

    private String parseCreativeRank(String rank) {
        switch (rank) {
            case "newbie" -> rank = "§aNybörjare";
            case "apprentice" -> rank = "§bLärling";
            case "experienced" -> rank = "§dErfaren!";
            case "expert" -> rank = "§5Expert";
            case "architect" -> rank = "§6Arkitekt";
        }
        return rank;
    }
}
