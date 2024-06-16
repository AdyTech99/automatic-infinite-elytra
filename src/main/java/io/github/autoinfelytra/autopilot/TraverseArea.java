package io.github.autoinfelytra.autopilot;

import io.github.autoinfelytra.AutomaticInfiniteElytra;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColumnPos;

import java.util.ArrayList;
import java.util.List;

public class TraverseArea {
    private static ColumnPos starting;
    private static ColumnPos ending;
    private static int current = 0;
    private static List<ColumnPos> coordinates = new ArrayList<>();

    public static void init(ColumnPos starting1, ColumnPos ending1){
        starting = starting1;
        ending = ending1;
        coordinates = getCoordinates(starting1, ending1);
        Autopilot.initNewFlight(AutomaticInfiniteElytra.blockPos(starting1), true);
        current = 0;
    }

    public static void stop(){
        coordinates.clear();
        starting = null;
        ending = null;
        current = 0;
    }

    public static boolean isTraversalInProgress(){
        return !coordinates.isEmpty();
    }
    public static void tick(){
        next();
    }
    private static void next(){
        current+=4;
        if(current >= coordinates.size()){
            stop();
            MinecraftClient.getInstance().player.sendMessage(Text.of("Done"));
            return;
        }
        Autopilot.unsetLocation();
        Autopilot.initNewFlight(AutomaticInfiniteElytra.blockPos(coordinates.get(current)), true);
        MinecraftClient.getInstance().player.sendMessage(Text.of(String.valueOf(coordinates.get(current))), true);
    }

    public static List<ColumnPos> getCoordinates(ColumnPos pos1, ColumnPos pos2) {
        List<ColumnPos> coordinates = new ArrayList<>();

        int minX = Math.min(pos1.x(), pos2.x());
        int maxX = Math.max(pos1.x(), pos2.x());
        int minZ = Math.min(pos1.z(), pos2.z());
        int maxZ = Math.max(pos1.z(), pos2.z());

        for (int x = minX; x <= maxX; x+=((MinecraftClient.getInstance().options.getClampedViewDistance()*16)-1)) {
            if (x % 2 == 0) {
                for (int z = minZ; z <= maxZ; z++) {
                    coordinates.add(new ColumnPos(x, z));
                }
            } else {
                for (int z = maxZ; z >= minZ; z--) {
                    coordinates.add(new ColumnPos(x, z));
                }
            }
        }

        return coordinates;
    }

    private static ColumnPos carryOver(ColumnPos pos){
        if(pos.x() >= ending.x()){
            return new ColumnPos(starting.x(), pos.z()+1);
        }
        if(pos.x() <= starting.x()){
            return new ColumnPos(starting.x(), pos.z()+1);
        }
        else return new ColumnPos(pos.x()+1, pos.z());
    }
}
