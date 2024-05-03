package io.github.autoinfelytra.hud;

import io.github.autoinfelytra.AutomaticInfiniteElytraClient;
import io.github.autoinfelytra.autopilot.Autopilot;
import io.github.autoinfelytra.config.AutomaticElytraConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.github.autoinfelytra.AutomaticInfiniteElytraClient.autoFlight;
import static io.github.autoinfelytra.AutomaticInfiniteElytraClient.getCurrentVelocity;

@Environment(EnvType.CLIENT)
public class HUDHelper {
    private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private static int altitude = 0;
    private static boolean isRunning = false;
    private static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public static void init(){
        service.scheduleWithFixedDelay(() -> {
            if(minecraftClient.player != null && AutomaticInfiniteElytraClient.showHud && !isRunning) {
                isRunning = true;
                altitude = altitude(minecraftClient.player);
                isRunning = false;
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    public static void exit(){
        service.shutdownNow();
        service = Executors.newSingleThreadScheduledExecutor();
    }
    public static ArrayList<String> generateHUD(ArrayList<String> hudArray, int HUD_ELEMENTS){
        assert minecraftClient.player != null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        /*if(!isRunning) executorService.submit(() -> {
            altitude = altitude(minecraftClient.player);
        });
        executorService.shutdown();*/

        ItemStack itemStack = minecraftClient.player.getEquippedStack(EquipmentSlot.CHEST);
        String[] hudString = new String[HUD_ELEMENTS];
        if (hudArray == null) hudArray = new ArrayList<String>();
        else hudArray.clear();

        if(AutomaticElytraConfig.HANDLER.instance().render_flight_mode) hudString[0] = "Flight mode: " + (autoFlight ? "Automatic" : "Manual");
        if(Autopilot.isLanding()) hudString[0] = hudString[0] + " Landing";
        if(Autopilot.isAutopilotRunning()) hudString[0] = hudString[0] + ", Autopilot running";

        if(AutomaticElytraConfig.HANDLER.instance().render_altitude) hudString[1] = "Altitude: " + altitude;
        if(AutomaticElytraConfig.HANDLER.instance().render_speed) hudString[2] = "Speed: " + String.format("%.2f", getCurrentVelocity() * 20) + " m/s";
        if(AutomaticElytraConfig.HANDLER.instance().render_elytra_durability) hudString[3] = "Elytra Durability: " + String.valueOf(itemStack.getMaxDamage() - itemStack.getDamage());
        if(Autopilot.isAutopilotRunning() && AutomaticElytraConfig.HANDLER.instance().render_autopilot_coords) hudString[4] = "Autopilot: " + Autopilot.getDestination().getX() + " " + Autopilot.getDestination().getZ() + " (" + Math.round(Math.pow(Autopilot.getDestination().getSquaredDistance(minecraftClient.player.getBlockPos()), 0.5)) + ")";

        for(int i = 0; i < HUD_ELEMENTS; i++){
            if(hudString[i] != null && !hudString[i].isEmpty()) hudArray.add(hudString[i]);
        }
        return hudArray;
    }

    private static int altitude(PlayerEntity player){
        World world = player.getWorld();
        BlockPos blockPos = player.getBlockPos();
        int counter = 0;
        while(world.getBlockState(blockPos).isAir() && !isOverVoid(blockPos)){
            blockPos = blockPos.down();
            counter++;
            if(isOverVoid(blockPos)) return player.getBlockY() - blockPos.getY();
            if(counter >= 20000) return Integer.MAX_VALUE;
        }
        return player.getBlockY() - blockPos.getY();
    }

    public static int getAltitude() {
        return altitude;
    }

    private static boolean isOverVoid(BlockPos blockPos){
        return blockPos.getY() < -64;
    }
}
