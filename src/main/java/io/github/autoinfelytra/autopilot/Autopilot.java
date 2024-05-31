package io.github.autoinfelytra.autopilot;

import io.github.autoinfelytra.AutomaticInfiniteElytraClient;
import io.github.autoinfelytra.config.AutomaticElytraConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class Autopilot {
    private static BlockPos destination;
    private static BlockPos prevDestination;
    private static PlayerEntity player;

    private static boolean turning = false;
    private static float targetYaw;
    private static float turnAmount;
    private static final int destinationLeeway = 16;
    private static int lastDistanceToDestination = Integer.MAX_VALUE;

    private static ScheduledExecutorService executorService;

    private static boolean landing;

    public static void init() {
        player = MinecraftClient.getInstance().player;
        turnAmount = (float) (6+Math.random());
        landing = false;
    }


    public static float getTargetYaw(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        Vec3d vec3d = anchorPoint.positionAt(player);
        double d = target.x - vec3d.x;
        double f = target.z - vec3d.z;
        return (MathHelper.wrapDegrees((float)(Math.atan2(f, d) * 57.2957763671875) - 90.0f));
    }

    public static void initNewFlight(BlockPos blockPos){
        if(AutomaticElytraConfig.HANDLER.instance().record_analytics){
            Autopilot.init();
            destination = blockPos;

            FlightAnalytics.setStartTime(player.age);
            FlightAnalytics.setDistance(((int) Math.sqrt(player.getBlockPos().getSquaredDistance(destination))));
            FlightAnalytics.setStartDurability(player.getEquippedStack(EquipmentSlot.CHEST).get(DataComponentTypes.DAMAGE));
            FlightAnalytics.startFlying();
        }
        setLocation(blockPos);
    }

    public static void setLocation(BlockPos blockPos) {
        Autopilot.init();
        destination = blockPos;
        prevDestination = destination.mutableCopy();
        targetYaw = getTargetYaw(EntityAnchorArgumentType.EntityAnchor.EYES, destination.toCenterPos());

    }

    public static void unsetLocation() {
        destination = null;
        turning = false;
    }

    public static void courseCorrection() {
        if (player.age % 200 == 0) setLocation(destination);
    }

    public static void target() {
        if (!MathHelper.approximatelyEquals(Math.abs(player.getYaw() - targetYaw), 0.0)) {
            player.setYaw(MathHelper.wrapDegrees(player.getYaw() + turnAmount));
            if(Math.abs(player.getYaw() - targetYaw) <= turnAmount * 2){
                player.setYaw(targetYaw);
            }
        }
        courseCorrection();
    }

    public static void tick() {
        if(player == null || player.getBlockPos() == null || destination == null || !player.isFallFlying()){
            destination = null;
            targetYaw = Integer.MIN_VALUE;
            lastDistanceToDestination = Integer.MAX_VALUE;
            return;
        }
        if (destination == null) return;
        if(!landing)
            if(executorService != null) executorService.shutdown();

        destination = new BlockPos(destination.getX(), player.getBlockY(), destination.getZ());
        if (isAtDestination()) {
            player.sendMessage(Text.literal("[Automatic Elytra Autopilot] You have arrived").formatted(Formatting.GREEN));


            if(AutomaticElytraConfig.HANDLER.instance().record_analytics) {
                FlightAnalytics.setTime((player.age - FlightAnalytics.getStartTime()) / 20);
                FlightAnalytics.setDurability_lost(player.getEquippedStack(EquipmentSlot.CHEST).get(DataComponentTypes.DAMAGE) - FlightAnalytics.getStartDurability());
                FlightAnalytics.flightDone();
                if(AutomaticElytraConfig.HANDLER.instance().auto_send_analytics) FlightAnalytics.printAnalytics(player);
            }

            if(AutomaticElytraConfig.HANDLER.instance().do_landing) {
                player.sendMessage(Text.literal("[Automatic Elytra Autopilot] Initiating landing procedures").formatted(Formatting.GREEN));
                destination = null;
                landing = true;
                initLanding();
            }
        }
        target();
    }

    public static void initLanding(){
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(Autopilot::land, 0, 20, TimeUnit.MILLISECONDS);
        if(!landing){
            executorService.shutdown();
        }
    }

    public static void land() {
        landing = landing
                && AutomaticInfiniteElytraClient.autoFlight
                && player.isFallFlying()
                && AutomaticElytraConfig.HANDLER.instance().do_landing
                && !player.isTouchingWater()
                && !player.isInLava();
        if(landing){
            player.setYaw((float) (player.getYaw() + turnAmount / 1.8));
        }
    }

    public static boolean isAtDestination(){
        if(Math.sqrt(player.getBlockPos().getSquaredDistance(destination)) > destinationLeeway) return false;
        if(Math.sqrt(player.getBlockPos().getSquaredDistance(destination)) == 0) return true;
        else {
            if(lastDistanceToDestination < Math.sqrt(player.getBlockPos().getSquaredDistance(destination))){
                return true;
            }
            else {
                lastDistanceToDestination = (int) Math.sqrt(player.getBlockPos().getSquaredDistance(destination));
                return false;
            }
        }
    }


    public static boolean isAutopilotRunning(){
        return destination != null;
    }

    public static boolean isLanding(){
        return landing;
    }

    public static BlockPos getDestination(){
        return destination.mutableCopy();
    }

    public static BlockPos getPrevDestination() {
        if(prevDestination != null) return prevDestination.mutableCopy();
        else return null;
    }
}
