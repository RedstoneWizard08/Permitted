package redstonedev.permitted.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraftforge.server.command.EnumArgument;
import redstonedev.permitted.data.PermitData;
import redstonedev.permitted.items.Permit;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PermitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> disp, CommandBuildContext ctx) {
        disp.register(literal("permit")
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
        );
    }
}
