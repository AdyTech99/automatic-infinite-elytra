package io.github.autoinfelytra;

import io.github.autoinfelytra.autopilot.Autopilot;
import io.github.autoinfelytra.autopilot.CollisionDetectionUtil;
import io.github.autoinfelytra.config.AutomaticElytraConfig;
import io.github.autoinfelytra.hud.HUD;
import io.github.autoinfelytra.hud.HUDHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class AutomaticInfiniteElytraClient implements net.fabricmc.api.ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("autoinfelytra");

    private static KeyBinding keyBinding;
    public static AutomaticInfiniteElytraClient instance;

    private static boolean lastPressed = false;

    public static final double DEFAULT_PULL_UP_ANGLE = -46.633514;
    public static final double DEFAULT_PULL_DOWN_ANGLE = 37.19872;
    public static final double DEFAULT_PULL_UP_MIN_VELOCITY = 1.9102669;
    public static final double DEFAULT_PULL_DOWN_MAX_VELOCITY = 2.3250866;
    public static final double DEFAULT_PULL_UP_SPEED = 2.1605124 * 3;
    public static final double DEFAULT_PULL_DOWN_SPEED = 0.20545267 * 3;

    // Flight parameters
    public static double pullUpAngle = DEFAULT_PULL_UP_ANGLE;
    public static double pullDownAngle = DEFAULT_PULL_DOWN_ANGLE;
    public static double pullUpMinVelocity = DEFAULT_PULL_UP_MIN_VELOCITY;
    public static double pullDownMaxVelocity = DEFAULT_PULL_DOWN_MAX_VELOCITY;
    public static double pullUpSpeed = DEFAULT_PULL_UP_SPEED;
    public static double pullDownSpeed = DEFAULT_PULL_DOWN_SPEED;
    public static final int rotationAmount = 180/CollisionDetectionUtil.scanAheadTicks;
    public static int rotationStage = 0;

    private static MinecraftClient minecraftClient;

    public static boolean showHud;
    public static boolean autoFlight;

    private static Vec3d previousPosition;
    private static double currentVelocity;

    public static boolean isDescending;
    public static boolean pullUp;
    public static boolean pullDown;
    public static boolean rotating = false;



    @Override
    public void onInitializeClient() {
        AutomaticElytraConfig.HANDLER.load();
        LOGGER.info("Sky's the beginning!");

        Commands.registerCommands();
        Autopilot.init();
        HUDHelper.init();

        keyBinding = new KeyBinding(
                "key.elytraautoflight.toggle", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_RIGHT_ALT, // The keycode of the key
                "text.elytraautoflight.title" // The translation key of the keybinding's category.
        );

        KeyBindingHelper.registerKeyBinding(keyBinding);

        lastPressed = false;
        ClientTickEvents.END_CLIENT_TICK.register(e -> {
            onTick();
            if(AutomaticElytraConfig.HANDLER.instance().autopilot) Autopilot.tick();
        });
        HudRenderCallback.EVENT.register(HUD::drawHUD);

        AutomaticInfiniteElytraClient.instance = this;
        LOGGER.info("I believe I can fly...");
    }

    public static void rotatePlayer(MinecraftClient minecraftClient){
        assert minecraftClient.player != null;
        Random random = new Random();
        int randomPitch = random.nextInt(2) - 1;
        if(rotating) {
            minecraftClient.player.setYaw((float) (minecraftClient.player.getYaw(0) + rotationAmount + (Math.random() * 2)));
            minecraftClient.player.setPitch(minecraftClient.player.getPitch() + randomPitch);
            minecraftClient.player.sendMessage(Text.literal("Taking evasive action! ").formatted(Formatting.RED), true);
            autoFlight = false;
            rotationStage++;
            minecraftClient.player.stopFallFlying();
        }
        if(rotationStage >= CollisionDetectionUtil.scanAheadTicks){
            rotating = false;
            rotationStage = 0;
            if(minecraftClient.player.checkFallFlying()) minecraftClient.player.startFallFlying();
        }
    }

    private static void onTick() {
        if(minecraftClient == null) minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.player != null) {
            rotatePlayer(minecraftClient);
            if (minecraftClient == null) minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient.player.isFallFlying()) showHud = true;
                else {
                    showHud = false;
                    autoFlight = false;
                }
                if (minecraftClient.getDebugHud().shouldShowDebugHud())
                    showHud = false;

            if (!lastPressed && keyBinding.isPressed()) {
                if (minecraftClient.player.isFallFlying()) {
                    // If the player is flying an elytra, we start the auto flight
                    autoFlight = !autoFlight;
                    if (autoFlight) isDescending = true;
                } else {
                    minecraftClient.player.sendMessage(Text.literal("[Automatic Infinite Elytra] ").formatted(Formatting.AQUA).append(Text.literal("You need to be flying!")).formatted(Formatting.RED), false); // Send a message to the player
                }
            }
            lastPressed = keyBinding.isPressed();


            if (autoFlight) {
                assert minecraftClient.world != null;
                if (AutomaticElytraConfig.HANDLER.instance().anti_collision)
                    CollisionDetectionUtil.cancelFlightIfObstacleDetected(minecraftClient.player, minecraftClient.world);
                if (minecraftClient.player.getY() >= AutomaticElytraConfig.HANDLER.instance().max_altitude)
                    isDescending = true;
                if (isDescending) {
                    pullUp = false;
                    pullDown = true;
                    if (currentVelocity >= pullDownMaxVelocity && AutomaticElytraConfig.HANDLER.instance().max_altitude >= minecraftClient.player.getY()) {
                        isDescending = false;
                        pullDown = false;
                        pullUp = true;
                    }
                } else {
                    pullUp = true;
                    pullDown = false;
                    if (currentVelocity <= pullUpMinVelocity) {
                        isDescending = true;
                        pullDown = true;
                        pullUp = false;
                    }
                }

                if (pullUp) {
                    minecraftClient.player.pitch -= pullUpSpeed;
                    if (minecraftClient.player.pitch <= pullUpAngle) minecraftClient.player.pitch = (float) pullUpAngle;
                }

                if (pullDown) {
                    minecraftClient.player.pitch += pullDownSpeed;
                    if (minecraftClient.player.pitch >= pullDownAngle)
                        minecraftClient.player.pitch = (float) pullDownAngle;
                }
            } else {
                pullUp = false;
                pullDown = false;
                HUD.hudColor = HUD.GREEN_HUD_COLOR;
            }


            HUD.tick();
            computeVelocity();
        }
        else{
            showHud = false;
            autoFlight = false;
            rotating = false;
        }
    }

    private static void computeVelocity()
    {
        Vec3d newPosition = minecraftClient.player.getPos();
        if (previousPosition == null)
            previousPosition = newPosition;
        Vec3d difference = new Vec3d(newPosition.x - previousPosition.x, newPosition.y - previousPosition.y, newPosition.z - previousPosition.z);
        previousPosition = newPosition;
        currentVelocity = difference.length();
    }

    public static double getCurrentVelocity(){
        return currentVelocity;
    }
}