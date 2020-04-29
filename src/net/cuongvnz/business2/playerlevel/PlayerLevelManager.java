package net.cuongvnz.business2.playerlevel;

import net.cuongvnz.business2.AbstractManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import net.cuongvnz.business2.PlayerLevel;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PlayerLevelManager extends AbstractManager {

	public HashMap<PlayerData, Player> profiles = new HashMap<>();

	public PlayerLevelManager(PlayerLevel pl) {
		super(pl);
	}

	@Override
	public void initialize() {
		reload();
	}

	public void reload(){

	}

	public void registerData(Player p){
		String uuid = p.getUniqueId().toString();
		FileConfiguration data = new YamlConfiguration();
		File file = new File(plugin.getDataFolder(), "data/"+uuid+".yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
				data.load(file);
				data.set("exp", 0.0);
				data.set("level", 1);
				data.save(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}else {
			try {
				data.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		PlayerData pd = new PlayerData(p);
		pd.exp = data.getDouble("exp");
		pd.level = data.getInt("level");
		profiles.put(pd, p);
	}

	public void unregisterData(Player p){
		String uuid = p.getUniqueId().toString();
		FileConfiguration data = new YamlConfiguration();
		File file = new File(plugin.getDataFolder(), "data/"+uuid+".yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
				data.load(file);
				data.set("exp", 0.0);
				data.set("level", 1);
				data.save(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}else {
			try {
				data.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		profiles.remove(p);
	}

    @EventHandler
    public void login(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		registerData(p);
    }

	@EventHandler
	public void logout(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		unregisterData(p);
	}

}
