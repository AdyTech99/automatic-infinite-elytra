package io.github.autoinfelytra.autopilot;

import io.github.autoinfelytra.AutomaticInfiniteElytraClient;
import io.github.autoinfelytra.config.AutomaticElytraConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Autopilot {
    private static BlockPos destination;
    private static BlockPos prevDestination;
    private static PlayerEntity player;

    private static boolean turning = false;
    private static float targetYaw;
    private static float turnAmount;
    private static final int destinationLeeway = 16;

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
        land();
        if(player == null || player.getBlockPos() == null){
            destination = null;
            targetYaw = Integer.MIN_VALUE;
            return;
        }
        if (!player.isFallFlying()) destination = null;
        if (destination == null) return;

        destination = new BlockPos(destination.getX(), player.getBlockY(), destination.getZ());
        if (Math.pow(player.getBlockPos().getSquaredDistance(destination), 0.5) <= destinationLeeway) {
            player.sendMessage(Text.literal("[Automatic Elytra Autopilot] You have arrived").formatted(Formatting.GREEN));
            if(AutomaticElytraConfig.HANDLER.instance().do_landing) player.sendMessage(Text.literal("[Automatic Elytra Autopilot] Initiating landing procedures").formatted(Formatting.GREEN));
            destination = null;
            landing = true;
        }
        target();
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
        return prevDestination.mutableCopy();
    }

    public static boolean isAtDestination(){
        return Math.pow(player.getBlockPos().getSquaredDistance(destination), 0.5) <= destinationLeeway;
    }
}
