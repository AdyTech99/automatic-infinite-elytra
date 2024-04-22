package io.github.autoinfelytra;

import io.github.autoinfelytra.config.AutomaticElytraConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
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

    private boolean lastPressed = false;

    public static final double DEFAULT_PULL_UP_ANGLE = -46.633514;
    public static final double DEFAULT_PULL_DOWN_ANGLE = 37.19872;
    public static final double DEFAULT_PULL_UP_MIN_VELOCITY = 1.9102669;
    public static final double DEFAULT_PULL_DOWN_MAX_VELOCITY = 2.3250866;
    public static final double DEFAULT_PULL_UP_SPEED = 2.1605124 * 3;
    public static final double DEFAULT_PULL_DOWN_SPEED = 0.20545267 * 3;

    // Flight parameters
    public double pullUpAngle = DEFAULT_PULL_UP_ANGLE;
    public double pullDownAngle = DEFAULT_PULL_DOWN_ANGLE;
    public double pullUpMinVelocity = DEFAULT_PULL_UP_MIN_VELOCITY;
    public double pullDownMaxVelocity = DEFAULT_PULL_DOWN_MAX_VELOCITY;
    public double pullUpSpeed = DEFAULT_PULL_UP_SPEED;
    public double pullDownSpeed = DEFAULT_PULL_DOWN_SPEED;
    public static final int rotationAmount = 180/CollisionDetectionUtil.scanAheadTicks;
    public static int rotationStage = 0;

    private MinecraftClient minecraftClient;

    public boolean showHud;
    public static boolean autoFlight;

    private Vec3d previousPosition;
    private double currentVelocity;

    public boolean isDescending;
    public boolean pullUp;
    public boolean pullDown;
    public boolean rotating = false;

    public final int RED_HUD_COLOR = Objects.requireNonNull(TextColor.fromFormatting(Formatting.RED)).getRgb();
    public final int YELLOW_HUD_COLOR = Objects.requireNonNull(TextColor.fromFormatting(Formatting.YELLOW)).getRgb();
    public final int GREEN_HUD_COLOR = Objects.requireNonNull(TextColor.fromFormatting(Formatting.GREEN)).getRgb();

    public ArrayList hudArray;
    public int hudColor = RED_HUD_COLOR;
    public final int HUD_ELEMENTS = 5;

    @Override
    public void onInitializeClient() {
        AutomaticElytraConfig.HANDLER.load();
        System.out.println("Sky's the beginning!");

        Commands.registerCommands();
        Autopilot.init();

        keyBinding = new KeyBinding(
                "key.elytraautoflight.toggle", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                "text.elytraautoflight.title" // The translation key of the keybinding's category.
        );

        KeyBindingHelper.registerKeyBinding(keyBinding);

        lastPressed = false;

        System.out.println("I believe I can fly...");
        ClientTickEvents.END_CLIENT_TICK.register(e -> {
            this.onTick();
            if(AutomaticElytraConfig.HANDLER.instance().autopilot) Autopilot.tick();
        });
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            drawContext.draw();

            //FLIGHT MODE
            if(hudArray != null && hudArray.size() >= 1) drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    (String) hudArray.get(0),
                    AutomaticElytraConfig.HANDLER.instance().x_coordinates_of_hud,
                    (int) (AutomaticElytraConfig.HANDLER.instance().y_coordinates_of_hud + AutomaticElytraConfig.HANDLER.instance().distance_between_sentences * -2),
                    hudColor,
                    true);

            //ALTITUDE
            if(hudArray != null && hudArray.size() >= 2) drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    (String) hudArray.get(1),
                    AutomaticElytraConfig.HANDLER.instance().x_coordinates_of_hud,
                    (int) (AutomaticElytraConfig.HANDLER.instance().y_coordinates_of_hud + AutomaticElytraConfig.HANDLER.instance().distance_between_sentences * -1),
                    hudColor,
                    true);

            //SPEED
            if(hudArray != null && hudArray.size() >= 3) drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    (String) hudArray.get(2),
                    AutomaticElytraConfig.HANDLER.instance().x_coordinates_of_hud,
                    (int) (AutomaticElytraConfig.HANDLER.instance().y_coordinates_of_hud + AutomaticElytraConfig.HANDLER.instance().distance_between_sentences * 0),
                    hudColor,
                    true);

            //ELYTRA DURABILITY
            if(hudArray != null && hudArray.size() >= 4) drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    (String) hudArray.get(3),
                    AutomaticElytraConfig.HANDLER.instance().x_coordinates_of_hud,
                    (int) (AutomaticElytraConfig.HANDLER.instance().y_coordinates_of_hud + AutomaticElytraConfig.HANDLER.instance().distance_between_sentences * 1),
                    hudColor,
                    true);

            //AUTOPILOT
            if(hudArray != null && hudArray.size() >= 5) drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    (String) hudArray.get(4),
                    AutomaticElytraConfig.HANDLER.instance().x_coordinates_of_hud,
                    (int) (AutomaticElytraConfig.HANDLER.instance().y_coordinates_of_hud + AutomaticElytraConfig.HANDLER.instance().distance_between_sentences * 2),
                    hudColor,
                    true);
        });

        AutomaticInfiniteElytraClient.instance = this;

    }

    public static void rotatePlayer(MinecraftClient minecraftClient){
        assert minecraftClient.player != null;
        Random random = new Random();
        int randomPitch = random.nextInt(2) - 1;
        if(instance.rotating) {
            minecraftClient.player.setYaw((float) (minecraftClient.player.getYaw(0) + rotationAmount + (Math.random() * 2)));
            minecraftClient.player.setPitch(minecraftClient.player.getPitch() + randomPitch);
            minecraftClient.player.sendMessage(Text.literal("Taking evasive action! ").formatted(Formatting.RED), true);
            autoFlight = false;
            rotationStage++;
            minecraftClient.player.stopFallFlying();
        }
        if(rotationStage >= CollisionDetectionUtil.scanAheadTicks){
            instance.rotating = false;
            rotationStage = 0;
            if(minecraftClient.player.checkFallFlying()) minecraftClient.player.startFallFlying();
        }
    }

    private void onTick() {
        if(minecraftClient == null) minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.player != null) {
            rotatePlayer(minecraftClient);
            if (minecraftClient == null) minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient.player.isFallFlying())
                    showHud = true;
                else {
                    showHud = false;
                    hudArray = null;
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
                hudColor = GREEN_HUD_COLOR;
            }


            if (showHud && AutomaticElytraConfig.HANDLER.instance().render_hud) {
                double altitude = minecraftClient.player.getPos().y;
                ItemStack itemStack = minecraftClient.player.getEquippedStack(EquipmentSlot.CHEST);
                String[] hudString = new String[HUD_ELEMENTS];
                if (hudArray == null) hudArray = new ArrayList<String>();
                else hudArray.clear();

                if(AutomaticElytraConfig.HANDLER.instance().render_flight_mode) hudString[0] = "Flight mode: " + (autoFlight ? "Automatic" : "Manual");
                if(Autopilot.isLanding()) hudString[0] = hudString[0] + " Landing";
                if(Autopilot.isAutopilotRunning()) hudString[0] = hudString[0] + ", Autopilot running";

                if(AutomaticElytraConfig.HANDLER.instance().render_altitude) hudString[1] = "Altitude: " + String.format("%.2f", altitude);
                if(AutomaticElytraConfig.HANDLER.instance().render_speed) hudString[2] = "Speed: " + String.format("%.2f", currentVelocity * 20) + " m/s";
                if(AutomaticElytraConfig.HANDLER.instance().render_elytra_durability) hudString[3] = "Elytra Durability: " + String.valueOf(itemStack.getMaxDamage() - itemStack.getDamage());
                if(Autopilot.isAutopilotRunning() && AutomaticElytraConfig.HANDLER.instance().render_autopilot_coords) hudString[4] = "Autopilot: " + Autopilot.getDestination().getX() + " " + Autopilot.getDestination().getY() + " " + Autopilot.getDestination().getZ() + " (" + Math.round(Math.pow(Autopilot.getDestination().getSquaredDistance(minecraftClient.player.getBlockPos()), 0.5)) + ")";

                for(int i = 0; i < HUD_ELEMENTS; i++){
                    if(hudString[i] != null && !hudString[i].isEmpty()) hudArray.add(hudString[i]);
                }

                if (autoFlight) hudColor = GREEN_HUD_COLOR;
                else hudColor = YELLOW_HUD_COLOR;
            } else hudArray = null;
            computeVelocity();
        }
    }

    private void computeVelocity()
    {
        Vec3d newPosition = minecraftClient.player.getPos();
        if (previousPosition == null)
            previousPosition = newPosition;
        Vec3d difference = new Vec3d(newPosition.x - previousPosition.x, newPosition.y - previousPosition.y, newPosition.z - previousPosition.z);
        previousPosition = newPosition;
        currentVelocity = difference.length();
    }
}