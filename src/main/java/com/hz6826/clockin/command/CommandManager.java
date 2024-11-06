package com.hz6826.clockin.command;

import com.hz6826.clockin.ClockIn;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.*;

public class CommandManager {
    private static final String rootCommand = "clockin";
    private static final String rootCommandAlias = "cin";
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
                    .then(literal("info")
                            .executes(context -> executeAsync(context, InfoCommand::showInfo))
                    )
                    .then(literal("leaderboard")
                            .executes(context -> executeAsync(context, LeaderboardCommand::showLeaderboard))
                    )
                    .then(literal("admin")
                            .requires(source -> source.hasPermissionLevel(4))
                            // <editor-fold desc="reward commands">
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
                            // </editor-fold>
                            // <editor-fold desc="money commands">
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
                            // </editor-fold>
                            // <editor-fold desc="raffleTicket commands">
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
                                    // </editor-fold>
                            // <editor-fold desc="makeupCard commands">
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
                            // </editor-fold>
                    )
                    // <editor-fold desc="utils commands">
                    .then(literal("showitem")
                            .executes(context -> executeAsync(context, UtilsCommand::showMainHandItem))
                    )
                    // </editor-fold>
                    // <editor-fold desc="economy commands">
                    .then(literal("economy")
                            .then(literal("balance").executes(context -> executeAsync(context, EconomyCommand::getBalance)))
                            .then(literal("deposit").executes(context -> executeAsync(context, EconomyCommand::deposit)))
                            .then(literal("withdraw").executes(context -> executeAsync(context, EconomyCommand::withdraw))
                                    .then(argument("amount", IntegerArgumentType.integer()).executes(context -> executeAsync(context, EconomyCommand::withdrawWithAmount))))
                            .then(literal("transfer")
                                    .then(argument("to", EntityArgumentType.player())
                                            .then(argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).executes(context -> executeAsync(context, EconomyCommand::transfer)))
                                    )
                            )
                    )
                    // </editor-fold>
                    // <editor-fold desc="mail commands">
                    .then(literal("mail")
                            .then(literal("send")
                                    .executes(context -> executeAsync(context, WIPCommand::WIP))
                            )
                            .then(literal("get")
                                    .executes(context -> executeAsync(context, MailCommand::getMails))
                                    .then(argument("page", IntegerArgumentType.integer())
                                            .executes(context -> executeAsync(context, MailCommand::getMailsWithPage))
                                    )
                            )
                            // TODO: fetch mail attachment command
                    )
                    // </editor-fold>
            );
            // <editor-fold desc="aliases">
            dispatcher.register(literal(rootCommand)
                    .then(literal("dci").executes(context -> executeAsync(context, DailyClockInCommand::dailyClockIn)))
                    .then(literal("i").executes(context -> executeAsync(context, LeaderboardCommand::showLeaderboard))
                    .then(literal("lb").executes(context -> executeAsync(context, LeaderboardCommand::showLeaderboard))))
                    .then(literal("a").redirect(clockInRootNode.getChild("admin")))
                    .then(literal("si").executes(context -> executeAsync(context, UtilsCommand::showMainHandItem)))
                    .then(literal("e").redirect(clockInRootNode.getChild("economy")))
            );
            dispatcher.register(literal(rootCommandAlias)
                    .then(literal("dailyclockin").executes(context -> executeAsync(context, DailyClockInCommand::dailyClockIn)))
                    .then(literal("info").executes(context -> executeAsync(context, InfoCommand::showInfo)))
                    .then(literal("leaderboard").executes(context -> executeAsync(context, LeaderboardCommand::showLeaderboard)))
                    .then(literal("admin").redirect(clockInRootNode.getChild("admin")))
                    .then(literal("showitem").executes(context -> executeAsync(context, UtilsCommand::showMainHandItem)))
                    .then(literal("economy").redirect(clockInRootNode.getChild("economy")))

                    .then(literal("dci").executes(context -> executeAsync(context, DailyClockInCommand::dailyClockIn)))
                    .then(literal("i").executes(context -> executeAsync(context, InfoCommand::showInfo)))
                    .then(literal("lb").executes(context -> executeAsync(context, LeaderboardCommand::showLeaderboard)))
                    .then(literal("a").redirect(clockInRootNode.getChild("admin")))
                    .then(literal("si").executes(context -> executeAsync(context, UtilsCommand::showMainHandItem)))
                    .then(literal("e").redirect(clockInRootNode.getChild("economy")))
            );
            // </editor-fold>
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
