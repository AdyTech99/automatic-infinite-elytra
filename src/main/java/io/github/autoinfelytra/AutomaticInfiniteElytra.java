package io.github.autoinfelytra;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;

public class AutomaticInfiniteElytra {
    public static BlockPos blockPos(ColumnPos columnPos){
        return new BlockPos(columnPos.x(), 0, columnPos.z());
    }
    public static ColumnPos columnPos(BlockPos blockPos){
        return new ColumnPos(blockPos.getX(), blockPos.getZ());
    }
}