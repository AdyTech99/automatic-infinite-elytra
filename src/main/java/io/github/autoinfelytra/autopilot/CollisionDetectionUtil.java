package io.github.autoinfelytra.autopilot;

import io.github.autoinfelytra.AutomaticInfiniteElytraClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CollisionDetectionUtil {
    public static final int scanAheadTicks = 5;

    public static void cancelFlightIfObstacleDetected(PlayerEntity player, World world) {
        Vec3d playerPos = player.getPos();
        Vec3d velocity = player.getVelocity();

        Vec3d scanVelocity = velocity.multiply(scanAheadTicks);
        Vec3d futurePos = playerPos.add(scanVelocity);

        Vec3i vec3i = new Vec3i((int) futurePos.x, (int) futurePos.y, (int) futurePos.z);
        BlockPos blockPos = new BlockPos(vec3i);
        if (world.getBlockState(blockPos).isSolid()) {
            player.sendMessage(Text.literal("[Collision Detection Utility] ").formatted(Formatting.AQUA).append(Text.literal("Flight aborted due to obstacle ahead!")), false); // Send a message to the player
            player.sendMessage(Text.literal("Consider using fireworks to boost your height before enabling automatic flight"));
            AutomaticInfiniteElytraClient.rotating = true;
        }
    }
}
