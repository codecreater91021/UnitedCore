package org.unitedlands.wars.war.health;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.unitedlands.wars.UnitedWars;
import org.unitedlands.wars.events.WarHealthChangeEvent;

import java.text.MessageFormat;

public class WarHealth {
    private int health = 100;
    private int maxHealth = 100;
    private final String name;
    private final BossBar bossBar = generateBossBar();
    public WarHealth(String name) {
        this.name = name;
    }

    public WarHealth(Town town) {
        this.name = town.getFormattedName();
    }

    public WarHealth(Nation nation) {
        this.name = nation.getFormattedName();
    }


    public BossBar getBossBar() {
        // make sure the health bar is updated
        updateHealthBar();
        return bossBar;
    }

    private BossBar generateBossBar() {
        String mainColor = getMainColor();
        String bracketColor = getBracketColor();
        Component name = getTitle(MessageFormat.format("{3}{0} <bold>HP: {4}[</bold>{3}{1}/{2}<bold>{4}]",
                this.name, this.health, this.maxHealth, mainColor, bracketColor));
        return BossBar.bossBar(name, 1F, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_10);
    }

    public void updateHealthBar() {
        BossBar old = bossBar;
        String mainColor = getMainColor();
        String bracketColor = getBracketColor();
        String name = MessageFormat.format("{3}{0} <bold>HP: {4}[</bold>{3}{1}/{2}<bold>{4}]",
                this.name, this.health, this.maxHealth, mainColor, bracketColor);
        bossBar.name(getTitle(name));
        bossBar.progress((float) health / 100F);
        bossBar.color(getBossBarColor());
        Bukkit.broadcastMessage(String.valueOf(old.equals(bossBar)));
    }

    private BossBar.Color getBossBarColor() {
        String mainColor = getMainColor();
        return switch (mainColor) {
            case "<gold>" -> BossBar.Color.YELLOW;
            case "<dark_red>" -> BossBar.Color.RED;
            default -> BossBar.Color.GREEN;
        };
    }

    public int getValue() {
        return health;
    }


    public void show(Player player) {
        player.showBossBar(bossBar);
    }
    public void hide(Player player) {
        player.hideBossBar(bossBar);
    }
    public void increaseHealth(int increment) {
        int newHealth = Math.min(maxHealth, health + increment);
        WarHealthChangeEvent whce = new WarHealthChangeEvent(this, health, maxHealth, newHealth, maxHealth);
        Bukkit.getServer().getPluginManager().callEvent(whce);
        this.health = newHealth;
        updateHealthBar();
    }

    public void decrementHealth(int decrement) {
        int newHealth = Math.max(0, health - decrement);
        WarHealthChangeEvent whce = new WarHealthChangeEvent(this, health, maxHealth, newHealth, maxHealth);
        Bukkit.getServer().getPluginManager().callEvent(whce);
        this.health = newHealth;
        updateHealthBar();
    }

    public void setHealth(int health) {
        WarHealthChangeEvent whce = new WarHealthChangeEvent(this, this.health, maxHealth, health, maxHealth);
        Bukkit.getServer().getPluginManager().callEvent(whce);
        this.health = health;
        updateHealthBar();
    }

    private Component getTitle(String message) {
        return UnitedWars.MINI_MESSAGE.deserialize(message);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        if (health == this.maxHealth) {
            setHealth(maxHealth);
        }
        this.maxHealth = Math.max(0, maxHealth);
        updateHealthBar();
    }

    public void decrementMaxHealth(int decrease) {
        setMaxHealth(this.maxHealth - decrease);
    }

    private String getMainColor() {
        if (health > 60) {
            return "<green>";
        } else if (health > 35) {
            return "<gold>";
        } else {
            return "<dark_red>";
        }
    }

    private String getBracketColor() {
        if (health > 60) {
            return "<dark_green>";
        } else if (health > 35) {
            return "<yellow>";
        } else {
            return "<dark_red>";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WarHealth warHealth = (WarHealth) o;

        if (health != warHealth.health) return false;
        if (maxHealth != warHealth.maxHealth) return false;
        if (!name.equals(warHealth.name)) return false;
        return bossBar.equals(warHealth.bossBar);
    }

    @Override
    public int hashCode() {
        int result = health;
        result = 31 * result + maxHealth;
        result = 31 * result + name.hashCode();
        result = 31 * result + bossBar.hashCode();
        return result;
    }
}
