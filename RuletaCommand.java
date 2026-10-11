package com.jousafio.command;

import com.jousafio.difficulty.DifficultyChange;
import com.jousafio.difficulty.DifficultyManager;
import com.jousafio.event.RuletaColor;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Arrays;
import java.util.List;

public class RuletaCommand {

    private static final SuggestionProvider<CommandSource> COLOR_SUGGESTIONS = (ctx, builder) ->
            ISuggestionProvider.suggest(Arrays.stream(RuletaColor.values()).map(c -> c.name().toLowerCase()), builder);

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("ruleta")
            .requires(s -> s.hasPermissionLevel(2))
            .then(Commands.argument("color", StringArgumentType.word())
                .suggests(COLOR_SUGGESTIONS)
                .then(Commands.argument("mensaje", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        String colorStr = StringArgumentType.getString(ctx, "color").toUpperCase();
                        String mensaje = StringArgumentType.getString(ctx, "mensaje");
                        RuletaColor color;
                        try { color = RuletaColor.valueOf(colorStr); }
                        catch (Exception e) {
                            ctx.getSource().sendErrorMessage(new StringTextComponent("Color invalido. Usa: amarilla naranja roja rosa verde azul morada celeste"));
                            return 0;
                        }
                        return execute(ctx.getSource(), color, mensaje);
                    })))
            .then(Commands.literal("aplicar")
                .then(Commands.argument("cambio", StringArgumentType.word())
                    .executes(ctx -> apply(ctx.getSource(), StringArgumentType.getString(ctx, "cambio")))))
            .then(Commands.literal("lista").executes(ctx -> { list(ctx.getSource()); return 1; }))
            .then(Commands.literal("mision").executes(ctx -> { dailyMission(ctx.getSource()); return 1; }))
            .then(Commands.literal("help").executes(ctx -> { help(ctx.getSource()); return 1; }))
        );
    }

    private static int execute(CommandSource source, RuletaColor color, String mensaje) {
        StringTextComponent title = new StringTextComponent("=== RULETA " + color.getDisplayName() + " ===").mergeStyle(color.getFormatting());
        StringTextComponent msg = new StringTextComponent(mensaje).mergeStyle(TextFormatting.WHITE);
        source.getServer().getPlayerList().getPlayers().forEach(p -> {
            p.sendStatusMessage(title, false);
            p.sendStatusMessage(msg, false);
        });
        switch (color) {
            case ROJA:
                source.getServer().getPlayerList().getPlayers().forEach(p -> {
                    p.sendStatusMessage(new StringTextComponent("!!! MOMENTO SAFIO ACTIVADO !!!").mergeStyle(TextFormatting.DARK_RED, TextFormatting.BOLD), false);
                    p.sendStatusMessage(new StringTextComponent("Cumple el objetivo o mueres...").mergeStyle(TextFormatting.RED), false);
                });
                break;
            case ROSA:
                source.getServer().getPlayerList().getPlayers().forEach(p -> {
                    p.sendStatusMessage(new StringTextComponent("!!! MOMENTO SAFION ACTIVADO !!!").mergeStyle(TextFormatting.LIGHT_PURPLE, TextFormatting.BOLD), false);
                    p.sendStatusMessage(new StringTextComponent("Minijuego iniciado...").mergeStyle(TextFormatting.LIGHT_PURPLE), false);
                });
                break;
            case NARANJA:
                showMenu(source);
                break;
            case AMARILLA:
                source.getServer().getPlayerList().getPlayers().forEach(p ->
                    p.sendStatusMessage(new StringTextComponent("Nuevo mob custom anadido").mergeStyle(TextFormatting.YELLOW), false));
                break;
            default:
                break;
        }
        source.sendFeedback(new StringTextComponent("Ruleta lanzada: " + color.getDisplayName()).mergeStyle(TextFormatting.GREEN), true);
        return 1;
    }

    private static void showMenu(CommandSource source) {
        source.sendFeedback(new StringTextComponent("=== MENU CAMBIOS DE DIFICULTAD ===").mergeStyle(TextFormatting.GOLD, TextFormatting.BOLD), false);
        for (DifficultyChange c : DifficultyManager.getAllChanges()) {
            StringTextComponent line = new StringTextComponent(" > [" + c.getId() + "] " + c.getName()).mergeStyle(TextFormatting.AQUA);
            line.setStyle(line.getStyle()
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ruleta aplicar " + c.getId()))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(c.getDescription()))));
            source.sendFeedback(line, false);
        }
    }

    private static int apply(CommandSource source, String id) {
        DifficultyChange c = DifficultyManager.getChangeById(id);
        if (c == null) {
            source.sendErrorMessage(new StringTextComponent("No existe: " + id));
            return 0;
        }
        DifficultyManager.applyChange(source.getServer(), c);
        source.getServer().getPlayerList().getPlayers().forEach(p -> {
            p.sendStatusMessage(new StringTextComponent("CAMBIO APLICADO: " + c.getName()).mergeStyle(TextFormatting.DARK_RED, TextFormatting.BOLD), false);
            p.sendStatusMessage(new StringTextComponent(c.getDescription()).mergeStyle(TextFormatting.RED), false);
        });
        return 1;
    }

    private static void list(CommandSource source) {
        for (DifficultyChange c : DifficultyManager.getAllChanges()) {
            source.sendFeedback(new StringTextComponent("- " + c.getId() + " : " + c.getName()).mergeStyle(TextFormatting.GRAY), false);
        }
    }

    private static void dailyMission(CommandSource source) {
        String[] missions = {
            "Consigue 16 diamantes",
            "Mata 10 hostiles sin morir",
            "Construye una base de 10x10",
            "Encuentra un Ancient City",
            "Crafta 3 totems de inmortalidad",
            "Sobrevive 10 minutos en el Nether",
            "Consigue full netherite"
        };
        String mission = missions[(int)(System.currentTimeMillis() % missions.length)];
        source.getServer().getPlayerList().getPlayers().forEach(p -> {
            p.sendStatusMessage(new StringTextComponent("=== MISION DIARIA ===").mergeStyle(TextFormatting.GOLD, TextFormatting.BOLD), false);
            p.sendStatusMessage(new StringTextComponent(mission).mergeStyle(TextFormatting.YELLOW), false);
            p.sendStatusMessage(new StringTextComponent("Completala para ganar recompensas!").mergeStyle(TextFormatting.GREEN), false);
        });
        source.sendFeedback(new StringTextComponent("Mision diaria anunciada: " + mission), true);
    }

    private static void help(CommandSource source) {
        source.sendFeedback(new StringTextComponent("=== JOUSAFIO ===").mergeStyle(TextFormatting.GOLD, TextFormatting.BOLD), false);
        source.sendFeedback(new StringTextComponent("/ruleta <color> <mensaje>"), false);
        source.sendFeedback(new StringTextComponent("/ruleta aplicar <id> | lista | mision | help"), false);
        source.sendFeedback(new StringTextComponent("ROJA=Momento Safio | ROSA=Momento Safion"), false);
    }
}
