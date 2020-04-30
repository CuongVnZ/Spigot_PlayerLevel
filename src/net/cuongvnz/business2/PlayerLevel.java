package net.cuongvnz.business2;

import java.io.File;

import net.cuongvnz.business2.menus.MenuManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.cuongvnz.business2.playerlevel.PlayerLevelManager;
import net.cuongvnz.business2.commands.CommandManager;

public class PlayerLevel extends JavaPlugin{


    public static PlayerLevel plugin;
	
    @Override
    public void onEnable() {
        plugin = this;
        
        File f = getDataFolder();
        if (!f.exists())
            f.mkdirs();
        
        saveDefaultConfig();

        // Instantiate Managers here
        new CommandManager(this);
        new PlayerLevelManager(this);
        new MenuManager(this);

        Settings.reload();
        plugin.getLogger().info("PlayerLevel by ChinnSu.");
    }
    
    @Override
    public void onDisable() {
        try {
            ManagerInstances.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
