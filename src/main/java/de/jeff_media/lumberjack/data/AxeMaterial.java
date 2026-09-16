package de.jeff_media.lumberjack.data;

import org.bukkit.Material;

import java.util.Locale;

public enum AxeMaterial {
    WOOD(1), STONE(2), COPPER(3), IRON(4), GOLD(5), DIAMOND(6), NETHERITE(7);

    private final int level;

    AxeMaterial(int level) {
        this.level = level;
    }

    public static boolean isAtLeast(Material axe, AxeMaterial requiredAxe) {
        AxeMaterial usedAxe = get(axe.name());
        return usedAxe.level >= requiredAxe.level;
    }

    public static AxeMaterial get(String materialName) {
        materialName = materialName.toUpperCase(Locale.ROOT);
        if(materialName.startsWith("W")) {
            return WOOD;
        } else if(materialName.startsWith("S")) {
            return STONE;
        } else if(materialName.startsWith("C")) {
            return COPPER;
        } else if(materialName.startsWith("I")) {
            return IRON;
        } else if(materialName.startsWith("G")) {
            return GOLD;
        } else if(materialName.startsWith("D")) {
            return DIAMOND;
        } else if(materialName.startsWith("N")) {
            return NETHERITE;
        }
        return WOOD;
    }
}
