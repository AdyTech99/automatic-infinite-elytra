package io.github.autoinfelytra.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.IntField;
import dev.isxander.yacl3.config.v2.api.autogen.MasterTickBox;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
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


    @SerialEntry(comment = "default = 20")
    @AutoGen(category = "Automatic_Elytra_Flight")
    @IntField(min = 0, max = Integer.MAX_VALUE)
    public Integer max_altitude = 300;

    @SerialEntry(comment = "should anti-collision utility be activated?")
    @AutoGen(category = "Automatic_Elytra_Flight")
    @MasterTickBox(value = "")
    public boolean anti_collision = true;

   // HUD MAIN

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Settings")
    @MasterTickBox(value = {
            "x_coordinates_of_hud",
            "y_coordinates_of_hud",
            "render_flight_mode",
            "render_altitude",
            "render_speed",
            "render_elytra_durability",
            "distance_between_sentences"
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
    @IntField(min = 0, max = Integer.MAX_VALUE)
    public Integer x_coordinates_of_hud = 20;

    @SerialEntry(comment = "default = 30")
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Appearance")
    @IntField(min = 0, max = Integer.MAX_VALUE)
    public Integer y_coordinates_of_hud = 30;

    @SerialEntry(comment = "default = 20")
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Appearance")
    @IntField(min = 0, max = Integer.MAX_VALUE)
    public Integer distance_between_sentences = 10;


    // AUTOPILOT
    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_Flight", group = "Autopilot_Main")
    @MasterTickBox(value = {
            "render_autopilot_coords",
            "do_landing"
    })
    public boolean autopilot = true;

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_Flight", group = "Autopilot_Main")
    @TickBox
    public boolean do_landing = true;

    @SerialEntry
    @AutoGen(category = "Automatic_Elytra_HUD", group = "HUD_Settings")
    @TickBox
    public boolean render_autopilot_coords = true;


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