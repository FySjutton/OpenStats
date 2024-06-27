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

    public informationList(int width, int height, String tabName, JsonObject data) {
        super(MinecraftClient.getInstance(), width, height - 24, 24, 25);

        this.data = data;

        ArrayList<String> selected = switch (tabName) {
            case "SURVIVAL" -> SURVIVAL;
            case "CREATIVE" -> CREATIVE;
            case "MB" -> MB;
            case "UHC" -> UHC;
            default -> INFO;
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
