package com.example.mod;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentString;

public class FillLimitCommand {

    public static int fillLimit = 32768;
    
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("fill")
                .then(literal("limit")
                        .executes(context -> getFillLimit(context.getSource()))
                        .then(argument("newLimit", IntegerArgumentType.integer(0))
                                .executes(context -> setFillLimit(context.getSource(), IntegerArgumentType.getInteger(context, "newLimit"))))));
    }
    
    private static int getFillLimit(CommandSource source) {
        source.sendFeedback(new TextComponentString("Fill limit: " + fillLimit), false);
        return fillLimit;
    }
    
    private static int setFillLimit(CommandSource source, int newLimit) {
        fillLimit = newLimit;
        source.sendFeedback(new TextComponentString("Fill limit updated to " + newLimit), true);
        return 0;
    }
    
}
