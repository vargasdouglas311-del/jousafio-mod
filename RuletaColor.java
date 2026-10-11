package com.jousafio.event;

import net.minecraft.util.text.TextFormatting;

public enum RuletaColor {
    AMARILLA("AMARILLA", TextFormatting.YELLOW),
    NARANJA("NARANJA", TextFormatting.GOLD),
    ROJA("ROJA (MOMENTO SAFIO)", TextFormatting.RED),
    ROSA("ROSA (MOMENTO SAFION)", TextFormatting.LIGHT_PURPLE),
    VERDE("VERDE", TextFormatting.GREEN),
    AZUL("AZUL", TextFormatting.BLUE),
    MORADA("MORADA", TextFormatting.DARK_PURPLE),
    CELESTE("CELESTE", TextFormatting.AQUA);

    private final String displayName;
    private final TextFormatting formatting;

    RuletaColor(String displayName, TextFormatting formatting) {
        this.displayName = displayName;
        this.formatting = formatting;
    }

    public String getDisplayName() { return displayName; }
    public TextFormatting getFormatting() { return formatting; }
}
