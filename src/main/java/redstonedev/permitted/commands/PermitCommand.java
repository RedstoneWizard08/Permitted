package redstonedev.permitted.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraftforge.server.command.EnumArgument;
import redstonedev.permitted.Permitted;
import redstonedev.permitted.data.PermitData;
import redstonedev.permitted.items.Permit;

import java.util.Optional;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PermitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> disp, CommandBuildContext ctx) {
        disp.register(literal("permit")
                .requires(cs -> cs.hasPermission(2))
                .then(literal("give")
                        .executes(cx -> {
                            var player = cx.getSource().getPlayer();

                            assert player != null;

                            player.addItem(Permitted.PERMIT.asStack());

                            return 1;
                        })
                )
                .then(literal("claim")
                        .executes(cx -> {
                            var player = cx.getSource().getPlayer();

                            if (player != null) {
                                var handItem = player.getMainHandItem();

                                if (handItem.getItem() instanceof Permit) {
                                    var permitData = PermitData.get(handItem);

                                    permitData.owner = Optional.of(player.getUUID());
                                    permitData.ownerName = Optional.of(player.getName().getString());

                                    PermitData.set(handItem, permitData);
                                }
                            }

                            return 1;
                        })
                        .then(argument("player", EntityArgument.player())
                                .executes(cx -> {
                                    var player = cx.getSource().getPlayer();
                                    var target = EntityArgument.getPlayer(cx, "player");

                                    if (player != null) {
                                        var handItem = player.getMainHandItem();

                                        if (handItem.getItem() instanceof Permit) {
                                            var permitData = PermitData.get(handItem);

                                            permitData.owner = Optional.of(target.getUUID());
                                            permitData.ownerName = Optional.of(target.getName().getString());

                                            PermitData.set(handItem, permitData);
                                        }
                                    }

                                    return 1;
                                })
                        )
                )
                .then(literal("unclaim")
                        .executes(cx -> {
                            var player = cx.getSource().getPlayer();

                            if (player != null) {
                                var handItem = player.getMainHandItem();

                                if (handItem.getItem() instanceof Permit) {
                                    var permitData = PermitData.get(handItem);

                                    permitData.owner = Optional.empty();
                                    permitData.ownerName = Optional.empty();

                                    PermitData.set(handItem, permitData);
                                }
                            }

                            return 1;
                        })
                )
                .then(literal("add")
                        .then(argument("item", ItemArgument.item(ctx))
                                .executes(cx -> {
                                    var item = cx.getArgument("item", ItemInput.class);
                                    var player = cx.getSource().getPlayer();

                                    if (player != null) {
                                        var handItem = player.getMainHandItem();

                                        if (handItem.getItem() instanceof Permit) {
                                            var permitData = PermitData.get(handItem);

                                            if (!permitData.items.contains(item.getItem())) {
                                                permitData.items.add(item.getItem());
                                                PermitData.set(handItem, permitData);
                                            }
                                        }
                                    }

                                    return 1;
                                })
                        )
                )
                .then(literal("remove")
                        .then(argument("item", ItemArgument.item(ctx))
                                .executes(cx -> {
                                    var item = cx.getArgument("item", ItemInput.class);
                                    var player = cx.getSource().getPlayer();

                                    if (player != null) {
                                        var handItem = player.getMainHandItem();

                                        if (handItem.getItem() instanceof Permit) {
                                            var permitData = PermitData.get(handItem);

                                            permitData.items.remove(item.getItem());
                                            PermitData.set(handItem, permitData);
                                        }
                                    }

                                    return 1;
                                })
                        )
                        .then(literal("all")
                                .executes(cx -> {
                                    var player = cx.getSource().getPlayer();

                                    if (player != null) {
                                        var handItem = player.getMainHandItem();

                                        if (handItem.getItem() instanceof Permit) {
                                            var permitData = PermitData.get(handItem);

                                            permitData.items.clear();
                                            PermitData.set(handItem, permitData);
                                        }
                                    }

                                    return 1;
                                })
                        )
                )
                .then(literal("rarity")
                        .then(argument("rarity", EnumArgument.enumArgument(PermitData.Rarity.class))
                                .executes(cx -> {
                                    var rarity = cx.getArgument("rarity", PermitData.Rarity.class);
                                    var player = cx.getSource().getPlayer();

                                    if (player != null) {
                                        var handItem = player.getMainHandItem();

                                        if (handItem.getItem() instanceof Permit) {
                                            var permitData = PermitData.get(handItem);

                                            permitData.rarity = rarity;
                                            PermitData.set(handItem, permitData);
                                        }
                                    }

                                    return 1;
                                })
                        )
                )
                .then(literal("name")
                        .then(argument("name", StringArgumentType.greedyString())
                                .executes(cx -> {
                                    var name = StringArgumentType.getString(cx, "name");
                                    var player = cx.getSource().getPlayer();

                                    if (player != null) {
                                        var handItem = player.getMainHandItem();

                                        if (handItem.getItem() instanceof Permit) {
                                            var permitData = PermitData.get(handItem);

                                            permitData.name = name;
                                            PermitData.set(handItem, permitData);
                                        }
                                    }

                                    return 1;
                                })
                        )
                )
        );
    }
}
