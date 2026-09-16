package de.jeff_media.lumberjack.hooks;

import de.jeff_media.lumberjack.LumberJack;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides %lumberjack_enabled%
 */
public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final LumberJack plugin;

    public PlaceholderAPIHook(LumberJack plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lumberjack";
    }

    @Override
    public @NotNull String getAuthor() {
        return "mfnalex";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (!params.equalsIgnoreCase("enabled")) return null;
        if (player == null || player.getPlayer() == null) return "false";
        return String.valueOf(plugin.getPlayerSetting(player.getPlayer()).gravityEnabled);
    }
}
