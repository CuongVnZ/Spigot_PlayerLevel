package net.cuongvnz.business2.commands;

import java.lang.reflect.Field;

import net.cuongvnz.business2.AbstractManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.cuongvnz.business2.PlayerLevel;
import net.cuongvnz.business2.commands.general.PlayerLevelCommand;

public class CommandManager extends AbstractManager {

    public static CommandMap cmap = null;

    public CommandManager(PlayerLevel plugin) {
        super(plugin);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().split(" ")[0].contains(":")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Hidden syntax is disabled.");
        }
    }

    @Override
    public void initialize() {
        try {
            Field f = getNMSClass("CraftServer").getDeclaredField("commandMap");
            f.setAccessible(true);
            cmap = (CommandMap) f.get(plugin.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cmap == null) {
            Log.error("FATAL ERROR: COULD NOT RETRIEVE COMMAND MAP.");
            plugin.getServer().shutdown();
            return;
        }
        AbstractCommand.plugin = plugin;
        register(null, new PlayerLevelCommand("playerlevel", "plv", "playerlv"));

    }

    protected void register(String perm, AbstractCommand command) {
        command.requiredPerm = perm;
        cmap.register("", command);
        plugin.getServer().getPluginManager().registerEvents(command, plugin);
    }

    public static Class<?> getNMSClass(String name) {
        String version = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
