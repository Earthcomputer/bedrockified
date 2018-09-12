package com.example.mod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentString;

public class FillLimitCommand {

    public static int fillLimit = 32768;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.func_197057_a("fill")
                .then(Commands.func_197057_a("limit")
                        .then(Commands.func_197056_a("newLimit", IntegerArgumentType.integer(0))
                                .executes(ctx -> setFillLimit(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "newLimit"))))));
    }

    private static int setFillLimit(CommandSource source, int newLimit) {
        fillLimit = newLimit;
        source.func_197030_a(new TextComponentString("Fill limit set to " + newLimit), true);
        return 0;
    }

}
