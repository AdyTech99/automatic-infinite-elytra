package io.github.autoinfelytra;

import com.mojang.brigadier.CommandDispatcher;
import dev.xpple.clientarguments.arguments.CColumnPosArgument;
import io.github.autoinfelytra.autopilot.Autopilot;
import io.github.autoinfelytra.autopilot.FlightAnalytics;
import io.github.autoinfelytra.config.AutomaticElytraConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;

public class Commands {
    public static void registerCommands(){
        ClientCommandRegistrationCallback.EVENT.register(Commands::SetDestinationCommand);
        ClientCommandRegistrationCallback.EVENT.register(Commands::SetLastDestinationCommand);
        CommandRegistrationCallback.EVENT.register(Commands::unsetDestinationCommand);
        CommandRegistrationCallback.EVENT.register(Commands::analyticsCommand);
    }

    private static void SetLastDestinationCommand(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register(ClientCommandManager.literal("setDestination")
            .executes(context -> {
                assert context.getSource().getPlayer() != null;
                BlockPos pos = Autopilot.getPrevDestination();
                if(pos != null){
                    if(AutomaticInfiniteElytraClient.autoFlight) {
                        Autopilot.initNewFlight(pos);
                        context.getSource().getPlayer().sendMessage(Text.literal("Autopilot is set to coordinates " + pos.getX() + " " + pos.getZ()).formatted(Formatting.GREEN));
                    }
                    else context.getSource().getPlayer().sendMessage(Text.literal("You need to be flying and have Automatic Flight Mode enabled.").formatted(Formatting.RED));
                }
                else context.getSource().getPlayer().sendMessage(Text.literal("Previous destination is null").formatted(Formatting.RED));
                return 1;
            }));
    }

    private static void SetDestinationCommand(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register(ClientCommandManager.literal("setDestination")
            .then(ClientCommandManager.argument("destination", CColumnPosArgument.columnPos())
                .executes(context -> {
                    assert context.getSource().getPlayer() != null;
                    //BlockPos pos = CBlockPosArgument.getBlockPos(context, "Z");
                    ColumnPos columnPos = CColumnPosArgument.getColumnPos(context, "destination");
                    BlockPos pos = new BlockPos(columnPos.x(), 0, columnPos.z());
                    if(true){
                        if(AutomaticInfiniteElytraClient.autoFlight) {
                            Autopilot.initNewFlight(pos);
                            context.getSource().getPlayer().sendMessage(Text.literal("Autopilot is set to coordinates " + pos.getX() + " " + pos.getZ()).formatted(Formatting.GREEN));
                        }
                        else context.getSource().getPlayer().sendMessage(Text.literal("You need to be flying and have Automatic Flight Mode enabled.").formatted(Formatting.RED));
                    }
                    else context.getSource().getPlayer().sendMessage(Text.literal("Autopilot is disabled. Please enable it in the Config.").formatted(Formatting.RED));
                   return 1;
        })));
    }

    private static void unsetDestinationCommand(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(CommandManager.literal("removeDestination")
                .executes(context -> {
                    Autopilot.unsetLocation();
                    context.getSource().getPlayer().sendMessage(Text.literal("Autopilot deactivated."));
                    return 0;
                }));
    }


    private static void analyticsCommand(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(CommandManager.literal("flightanalytics")
                .executes(context -> {
                    if(FlightAnalytics.isCompletedFlight() && AutomaticElytraConfig.HANDLER.instance().record_analytics){
                        context.getSource().getPlayer().sendMessage(Text.literal("-"));
                        context.getSource().getPlayer().sendMessage(Text.literal("Getting Analytics"));
                        FlightAnalytics.printAnalytics(context.getSource().getPlayer());
                    }
                    else {
                        context.getSource().getPlayer().sendMessage(Text.literal("Flight data is unavailable").formatted(Formatting.RED));
                        context.getSource().getPlayer().sendMessage(Text.literal("This might be because you haven't flown yet, or Analytics is disabled in your config").formatted(Formatting.WHITE));
                    }
                    return 0;
                }));
    }
}
