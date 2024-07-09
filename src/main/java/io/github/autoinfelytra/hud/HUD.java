package io.github.autoinfelytra.hud;

import io.github.autoinfelytra.AutomaticInfiniteElytraClient;
import io.github.autoinfelytra.config.AutomaticElytraConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Objects;
@Environment(EnvType.CLIENT)
public class HUD {

    public static final int RED_HUD_COLOR = Objects.requireNonNull(TextColor.fromFormatting(Formatting.RED)).getRgb();
    public static final int YELLOW_HUD_COLOR = Objects.requireNonNull(TextColor.fromFormatting(Formatting.YELLOW)).getRgb();
    public static final int GREEN_HUD_COLOR = Objects.requireNonNull(TextColor.fromFormatting(Formatting.GREEN)).getRgb();

    public static ArrayList<String> hudArray;
    public static int hudColor = RED_HUD_COLOR;
    public static final int HUD_ELEMENTS = 5;

    public static void tick(){
        if (AutomaticInfiniteElytraClient.showHud && AutomaticElytraConfig.HANDLER.instance().render_hud) {
            hudArray = HUDHelper.generateHUD(hudArray, HUD_ELEMENTS);
            if (AutomaticInfiniteElytraClient.autoFlight) hudColor = GREEN_HUD_COLOR;
            else hudColor = YELLOW_HUD_COLOR;
        } else hudArray = null;
    }
    public static void drawHUD(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        drawContext.draw();

        int x = (MinecraftClient.getInstance().getWindow().getWidth() * AutomaticElytraConfig.HANDLER.instance().x_coordinates_of_hud)/2000;
        int y = ((MinecraftClient.getInstance().getWindow().getHeight()) * AutomaticElytraConfig.HANDLER.instance().y_coordinates_of_hud)/2000;

        //MinecraftClient.getInstance().player.sendMessage(Text.literal(String.valueOf((MinecraftClient.getInstance().getWindow().getHeight()))), true);

        //FLIGHT MODE
        if(hudArray != null && hudArray.size() >= 1) drawContext.drawText(
                MinecraftClient.getInstance().textRenderer,
                hudArray.get(0),
                x,
                (int) (y + AutomaticElytraConfig.HANDLER.instance().distance_between_sentences * -2),
                hudColor,
                true);

        //ALTITUDE
        if(hudArray != null && hudArray.size() >= 2) drawContext.drawText(
                MinecraftClient.getInstance().textRenderer,
                hudArray.get(1),
                x,
                (int) (y + AutomaticElytraConfig.HANDLER.instance().distance_between_sentences * -1),
                hudColor,
                true);

        //SPEED
        if(hudArray != null && hudArray.size() >= 3) drawContext.drawText(
                MinecraftClient.getInstance().textRenderer,
                hudArray.get(2),
                x,
                (int) (y + AutomaticElytraConfig.HANDLER.instance().distance_between_sentences * 0),
                hudColor,
                true);

        //ELYTRA DURABILITY
        if(hudArray != null && hudArray.size() >= 4) drawContext.drawText(
                MinecraftClient.getInstance().textRenderer,
                hudArray.get(3),
                x,
                (int) (y + AutomaticElytraConfig.HANDLER.instance().distance_between_sentences * 1),
                hudColor,
                true);

        //AUTOPILOT
        if(hudArray != null && hudArray.size() >= 5) drawContext.drawText(
                MinecraftClient.getInstance().textRenderer,
                hudArray.get(4),
                x,
                (int) (y + AutomaticElytraConfig.HANDLER.instance().distance_between_sentences * 2),
                hudColor,
                true);
    }
}
