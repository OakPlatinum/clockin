package com.hz6826.clockin.command;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.api.FabricUtils;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.*;

public class CommandManager {
    public static String CURRENCY_NAME = BasicConfig.getConfig().getCurrencyName();
    private static final ArrayList<ServerPlayerEntity> waitingPlayers = new ArrayList<>();
    public CommandManager() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            final LiteralCommandNode<ServerCommandSource> clockInRootNode = dispatcher.register(literal("clockin")
                    .executes(context -> {
                        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.clockin.loading"), false);
                        return 1;
                    })
                    .then(literal("dailyclockin")
                            .executes(context -> executeAsync(context, DailyClockInCommand::dailyClockIn)))
                    .then(literal("calendar")
                            .executes(context -> {
                                context.getSource().sendFeedback(() -> Text.translatable("command.clockin.error.work_in_progress").formatted(Formatting.RED), false);
                                return 1;
                            })
                    )
                    .then(literal("admin")
                            .requires(source -> source.hasPermissionLevel(4))
                            .then(literal("reward")
                                    .then(literal("get")
                                            .then(argument("key", StringArgumentType.string()).executes(context -> executeAsync(context, AdminCommand::getReward)))
                                    )
                                    .then(literal("set")
                                            .then(argument("key", StringArgumentType.string())
                                                    .then(literal("itemList")
                                                            .then(literal("fromHot-bar").executes(context -> executeAsync(context, AdminCommand::setRewardItemListFromHotBar)))
                                                            .then(literal("fromInventory").executes(context -> executeAsync(context, AdminCommand::setRewardItemListFromInventory)))
                                                            .then(literal("fromMainHand").executes(context -> executeAsync(context, AdminCommand::setRewardItemListFromMainHand)))
                                                    )
                                                    .then(literal("money")
                                                            .then(argument("amount", DoubleArgumentType.doubleArg()).executes(context -> executeAsync(context, AdminCommand::setRewardMoney)))
                                                    )
                                                    .then(literal("raffleTicket")
                                                            .then(argument("amount", IntegerArgumentType.integer()).executes(context -> executeAsync(context, AdminCommand::setRewardRaffleTicket)))
                                                    )
                                                    .then(literal("makeupCard")
                                                            .then(argument("amount", IntegerArgumentType.integer()).executes(context -> executeAsync(context, AdminCommand::setRewardMakeupCard)))
                                                    )
                                            )
                                    )
                                    .then(literal("give")
                                            .then(argument("player", EntityArgumentType.player())
                                                    .then(argument("key", StringArgumentType.string()).executes(context -> executeAsync(context, AdminCommand::giveRewardToPlayer)))
                                            )
                                    )
                            )
                            .then(literal("money")
                                    .then(literal("get")
                                            .then(argument("player", EntityArgumentType.player()).executes(context -> executeAsync(context, AdminCommand::getPlayerBalance)))
                                    )
                                    .then(literal("set")
                                            .then(argument("player", EntityArgumentType.player())
                                                    .then(argument("amount", DoubleArgumentType.doubleArg()).executes(context -> executeAsync(context, AdminCommand::setPlayerBalance)))
                                            )
                                    )
                                    .then(literal("give")
                                            .then(argument("player", EntityArgumentType.player())
                                                    .then(argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).executes(context -> executeAsync(context, AdminCommand::addPlayerBalance)))
                                            )
                                    )
                                    .then(literal("take")
                                            .then(argument("player", EntityArgumentType.player())
                                                    .then(argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).executes(context -> executeAsync(context, AdminCommand::subtractPlayerBalance)))
                                            )
                                    )
                            )
                            .then(literal("raffleTicket")
                                    .then(literal("get")
                                            .then(argument("player", EntityArgumentType.player()).executes(context -> executeAsync(context, AdminCommand::getPlayerRaffleTicket)))
                                    )
                                    .then(literal("set")
                                            .then(argument("player", EntityArgumentType.player())
                                                    .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> executeAsync(context, AdminCommand::setPlayerRaffleTicket)))
                                            )
                                    )
                                    .then(literal("give")
                                            .then(argument("player", EntityArgumentType.player())
                                                    .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> executeAsync(context, AdminCommand::addPlayerRaffleTicket)))
                                            )
                                    )
                                    .then(literal("take")
                                            .then(argument("player", EntityArgumentType.player())
                                                    .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> executeAsync(context, AdminCommand::removePlayerRaffleTicket)))
                                            )
                                    )
                            )
                            .then(literal("makeupCard")
                                    .then(literal("get")
                                            .then(argument("player", EntityArgumentType.player()).executes(context -> executeAsync(context, AdminCommand::getPlayerMakeupCard)))
                                    )
                                    .then(literal("set")
                                            .then(argument("player", EntityArgumentType.player())
                                                    .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> executeAsync(context, AdminCommand::setPlayerMakeupCard)))
                                            )
                                    )
                                    .then(literal("give")
                                            .then(argument("player", EntityArgumentType.player())
                                                    .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> executeAsync(context, AdminCommand::addPlayerMakeupCard)))
                                            )
                                    )
                                    .then(literal("take")
                                            .then(argument("player", EntityArgumentType.player())
                                                    .then(argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> executeAsync(context, AdminCommand::removePlayerMakeupCard)))
                                            )
                                    )
                            )
                    )
                    .then(literal("item")
                            .executes(context -> executeAsync(context, UtilsCommand::showMainHandItem))
                    )
                    .then(literal("economy")
                            .then(literal("balance").executes(context -> executeAsync(context, EconomyCommand::getBalance)))
                            .then(literal("deposit").executes(context -> executeAsync(context, EconomyCommand::deposit)))
                            .then(literal("withdraw").executes(context -> executeAsync(context, EconomyCommand::withdraw))
                                    .then(argument("amount", IntegerArgumentType.integer()).executes(context -> executeAsync(context, EconomyCommand::withdrawWithAmount))))
                            .then(literal("transfer")
                                    .then(argument("player", EntityArgumentType.player())
                                            .then(argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).executes(context -> executeAsync(context, EconomyCommand::transfer)))
                                    )
                            )
                    )
            );
            dispatcher.register(literal("ci").redirect(clockInRootNode));
        }));


    }

    public int executeAsync(CommandContext<ServerCommandSource> context, CommandFunctionalInterface function){
        if (waitingPlayers.contains(context.getSource().getPlayer())) {
            context.getSource().sendError(Text.translatable("command.clockin.error.already_running"));
            return 0;
        }
        context.getSource().sendFeedback(() -> Text.translatable("command.clockin.executing"), false);
        CompletableFuture.runAsync(() -> {
            try {
                waitingPlayers.add(context.getSource().getPlayer());
                function.execute(context);
            } catch (Exception e) {
                context.getSource().sendError(Text.translatable("command.clockin.error"));
                ClockIn.LOGGER.error("Error while executing dailyclockin command: " + e.getMessage());
            }
            waitingPlayers.remove(context.getSource().getPlayer());
        });
        return 1;
    }
}
