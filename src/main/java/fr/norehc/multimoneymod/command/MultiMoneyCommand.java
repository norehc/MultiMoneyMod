package fr.norehc.multimoneymod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.norehc.multimoneymod.MultiMoneyMod;
import fr.norehc.multimoneymod.fileManager.ConfigManager;
import fr.norehc.multimoneymod.fileManager.PlayerMoneyFileManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MultiMoneyCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("multimoney")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("add")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .suggests(MultiMoneyCommand::suggestName)
                                                .then(Commands.argument("quantity", DoubleArgumentType.doubleArg())
                                                        .executes(context -> addMoney(
                                                                context,
                                                                EntityArgument.getPlayer(context, "player"),
                                                                StringArgumentType.getString(context, "name"),
                                                                DoubleArgumentType.getDouble(context, "quantity"))
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .suggests(MultiMoneyCommand::suggestName)
                                                .then(Commands.argument("quantity", DoubleArgumentType.doubleArg())
                                                        .executes(context -> removeMoney(
                                                                context,
                                                                EntityArgument.getPlayer(context, "player"),
                                                                StringArgumentType.getString(context, "name"),
                                                                DoubleArgumentType.getDouble(context, "quantity"))
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("set")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .suggests(MultiMoneyCommand::suggestName)
                                                .then(Commands.argument("quantity", DoubleArgumentType.doubleArg())
                                                        .executes(context -> {
                                                                    setMoney(
                                                                            context,
                                                                            EntityArgument.getPlayer(context, "player"),
                                                                            StringArgumentType.getString(context, "name"),
                                                                            DoubleArgumentType.getDouble(context, "quantity"));
                                                                    return 1;
                                                                }
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("reload")
                                .requires(src -> src.hasPermission(2))
                                .executes(context -> {
                                            reload();
                                            return 1;
                                        }
                                )
                        )
                        .then(Commands.literal("resetall")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests(MultiMoneyCommand::suggestName)
                                        .executes(context -> {
                                                    resetMoney(
                                                            context,
                                                            StringArgumentType.getString(context, "name"));
                                                    return 1;
                                                }
                                        )
                                )
                        )
                        .then(Commands.literal("removedata")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests(MultiMoneyCommand::suggestName)
                                        .executes(context -> {
                                                removeDataMoney(
                                                            context,
                                                            StringArgumentType.getString(context, "name"));
                                                    return 1;
                                                }
                                        )
                                )
                        )
        );
    }

    public static int addMoney(CommandContext<CommandSource> context, ServerPlayerEntity player, String name, double number) {
        modifyData(context, player, name, number);
        return 1;
    }

    public static int removeMoney(CommandContext<CommandSource> context, ServerPlayerEntity player, String name, double number) {
        if (number < 0) {
            modifyData(context, player, name, number);
        } else {
            modifyData(context, player, name, number * -1);
        }
        return 1;
    }

    public static void setMoney(CommandContext<CommandSource> context, ServerPlayerEntity player, String name, double number) {
        if (!MultiMoneyMod.playerMoney.containsKey(player.getUUID()) || ConfigManager.getIdMoneyFromName(name) == -1) {
            return;
        }

        int idMoney = ConfigManager.getIdMoneyFromName(name);

        if (!MultiMoneyMod.playerMoney.get(player.getUUID()).containsKey(idMoney)) {
            MultiMoneyMod.playerMoney.get(player.getUUID()).put(idMoney, number < 0 ? 0 : number);
        } else {
            MultiMoneyMod.playerMoney.get(player.getUUID()).replace(idMoney, number);
        }
    }

    private static void modifyData(CommandContext<CommandSource> context, ServerPlayerEntity player, String name, double number) {
        if (!MultiMoneyMod.playerMoney.containsKey(player.getUUID()) || ConfigManager.getIdMoneyFromName(name) == -1) {
            return;
        }

        int idMoney = ConfigManager.getIdMoneyFromName(name);

        if (!MultiMoneyMod.playerMoney.get(player.getUUID()).containsKey(idMoney)) {
            MultiMoneyMod.playerMoney.get(player.getUUID()).put(idMoney, number < 0 ? 0 : number);
        } else {
            double possesMoney = MultiMoneyMod.playerMoney.get(player.getUUID()).get(idMoney);
            MultiMoneyMod.playerMoney.get(player.getUUID()).replace(idMoney, possesMoney + number);
        }
    }

    public static void removeDataMoney(CommandContext<CommandSource> context, String name) {
        if (ConfigManager.getIdMoneyFromName(name) == -1) {
            return;
        }

        int idMoney = ConfigManager.getIdMoneyFromName(name);

        Map<UUID, Map<Integer, Double>> playersData = PlayerMoneyFileManager.loadAllStats();

        playersData.forEach(((uuid, integerDoubleMap) -> {
            integerDoubleMap.remove(idMoney);
            if(MultiMoneyMod.playerMoney.containsKey(uuid)) {
                MultiMoneyMod.playerMoney.get(uuid).remove(idMoney);
            }
            PlayerMoneyFileManager.saveStats(uuid, integerDoubleMap);
        }));
    }

    public static void resetMoney(CommandContext<CommandSource> context, String name) {
        if (ConfigManager.getIdMoneyFromName(name) == -1) {
            return;
        }

        int idMoney = ConfigManager.getIdMoneyFromName(name);

        Map<UUID, Map<Integer, Double>> playersData = PlayerMoneyFileManager.loadAllStats();

        playersData.forEach(((uuid, integerDoubleMap) -> {
            integerDoubleMap.replace(idMoney, 0.0);
            if(MultiMoneyMod.playerMoney.containsKey(uuid)) {
                MultiMoneyMod.playerMoney.get(uuid).replace(idMoney, 0.0);
            }
            PlayerMoneyFileManager.saveStats(uuid, integerDoubleMap);
        }));
    }

    private static CompletableFuture<Suggestions> suggestName(
            CommandContext<CommandSource> context,
            SuggestionsBuilder builder
    ) {
        return ISuggestionProvider.suggest(
                ConfigManager.getAllMoneysName(),
                builder
        );
    }

    private static void reload() {
        MultiMoneyMod.LOGGER.info("§4Reload des ressources du mod");
        ConfigManager.loadConfig();
        MultiMoneyMod.LOGGER.info("§4Fin du reload des ressources du mod");
    }
}
