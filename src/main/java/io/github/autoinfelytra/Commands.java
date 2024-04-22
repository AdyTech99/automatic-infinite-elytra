package io.github.autoinfelytra;

import com.mojang.brigadier.CommandDispatcher;
import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;
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

public class Commands {
    public static void registerCommands(){
        ClientCommandRegistrationCallback.EVENT.register(Commands::SetDestinationCommand);
        ClientCommandRegistrationCallback.EVENT.register(Commands::SetLastDestinationCommand);
        CommandRegistrationCallback.EVENT.register(Commands::unsetDestinationCommand);
    }

    private static void SetLastDestinationCommand(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register(ClientCommandManager.literal("setDestination")
            .executes(context -> {
                assert context.getSource().getPlayer() != null;
                BlockPos pos = Autopilot.getPrevDestination();
                if(AutomaticElytraConfig.HANDLER.instance().autopilot){
                    if(AutomaticInfiniteElytraClient.autoFlight) {
                        Autopilot.setLocation(pos);
                        context.getSource().getPlayer().sendMessage(Text.literal("Autopilot is set to coordinates " + pos.getX() + " " + pos.getY() + " " + pos.getZ()).formatted(Formatting.GREEN));
                    }
                    else context.getSource().getPlayer().sendMessage(Text.literal("You need to be flying and have Automatic Flight Mode enabled.").formatted(Formatting.RED));
                }
                else context.getSource().getPlayer().sendMessage(Text.literal("Autopilot is disabled. Please enable it in the Config.").formatted(Formatting.RED));
                return 1;
            }));
    }

    private static void SetDestinationCommand(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register(ClientCommandManager.literal("setDestination")
            .then(ClientCommandManager.argument("Z", CBlockPosArgumentType.blockPos())
                .executes(context -> {
                    assert context.getSource().getPlayer() != null;
                    BlockPos pos = CBlockPosArgumentType.getCBlockPos(context, "Z");
                    if(AutomaticElytraConfig.HANDLER.instance().autopilot){
                        if(AutomaticInfiniteElytraClient.autoFlight) {
                            Autopilot.setLocation(pos);
                            context.getSource().getPlayer().sendMessage(Text.literal("Autopilot is set to coordinates " + pos.getX() + " " + pos.getY() + " " + pos.getZ()).formatted(Formatting.GREEN));
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
}
