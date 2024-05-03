package io.github.autoinfelytra.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import io.github.autoinfelytra.hud.HUDHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class AutomaticElytraConfig {
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("automatic_elytra_config.json");

    public static final ConfigClassHandler<AutomaticElytraConfig> HANDLER = ConfigClassHandler.createBuilder(AutomaticElytraConfig.class)
            .id(new Identifier("automatic-elytra", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(CONFIG_PATH)
                    .build())
            .build();

    public final int windowWidth = 1000;
    public final int windowHeight = 1000;




    @SerialEntry(comment = "default = 20")
    @AutoGen(category = "Automatic_Elytra_Flight", group = "Autoflight")
    @IntField(min = 0, max = Integer.MAX_VALUE)
    public Integer max_altitude = 200;

    @SerialEntry(comment = "should anti-collision utility be activated?")
    @AutoGen(category = "Automatic_Elytra_Flight", group = "Autoflight")
    @MasterTickBox(value = "")
    public boolean anti_collision = true;

   // HUDHelper MAIN

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Settings")
    @MasterTickBox(value = {
            "x_coordinates_of_hud",
            "y_coordinates_of_hud",
            "render_flight_mode",
            "render_altitude",
            "render_speed",
            "render_elytra_durability",
            "distance_between_sentences",
            "render_autopilot_coords"
    })
    public boolean render_hud = true;

    //RENDER FINE TUNES

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Settings")
    @TickBox
    public boolean render_flight_mode = true;

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Settings")
    @TickBox
    public boolean render_altitude = true;

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Settings")
    @TickBox
    public boolean render_speed = true;

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Settings")
    @TickBox
    public boolean render_elytra_durability = true;

    //COORDINATES

    @SerialEntry(comment = "default = 20")
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Appearance")
    @IntField(min = 0)
    public Integer x_coordinates_of_hud = 20;

    @SerialEntry(comment = "default = 30")
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Appearance")
    @IntField(min = 0)
    public Integer y_coordinates_of_hud = 30;

    @SerialEntry(comment = "default = 20")
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Appearance")
    @IntField(min = 0, max = Integer.MAX_VALUE)
    public Integer distance_between_sentences = 10;


    // AUTOPILOT

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_Flight", group = "Autopilot")
    @TickBox
    public boolean do_landing = true;

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Settings")
    @TickBox
    public boolean render_autopilot_coords = true;

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_Flight", group = "Autopilot")
    @MasterTickBox(value = "auto_send_analytics")
    public boolean record_analytics = true;

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_Flight", group = "Autopilot")
    @TickBox
    public boolean auto_send_analytics = false;

    public static Screen createScreen(@Nullable Screen parent) {
        return HANDLER.generateGui().generateScreen(parent);
    }
    public Screen createConfigScreen(Screen parent) {
        if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
            return createScreen(parent);
        }
        return null;
    }
}