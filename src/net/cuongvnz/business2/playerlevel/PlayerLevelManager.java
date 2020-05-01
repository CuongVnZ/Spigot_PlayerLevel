package net.cuongvnz.business2.playerlevel;

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import net.cuongvnz.business2.AbstractManager;
import net.cuongvnz.business2.Settings;
import net.cuongvnz.business2.playerlevel.papi.LevelHolder;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import net.cuongvnz.business2.PlayerLevel;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerLevelManager extends AbstractManager {

	public static HashMap<Player, PlayerData> profiles = new HashMap<>();

	public PlayerLevelManager(PlayerLevel pl) {
		super(pl);
	}

	@Override
	public void initialize() {
		reload();
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new LevelHolder(plugin).register();
		}
	}

	public static void reload(){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(profiles.get(p) == null){
				registerData(p);
			}
		}
	}

	public static void registerData(Player p){
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
		profiles.put(p, pd);
	}

	public static void unregisterData(Player p){
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
				PlayerData pd = profiles.get(p);
				data.load(file);
				data.set("exp",  pd.exp);
				data.set("level", pd.level);
				data.save(file);
			} catch (IOException | InvalidConfigurationException e) {
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

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		Player p = event.getPlayer();
		PlayerData pd = profiles.get(p);
		if(pd == null) return;
		for(ChatPrefix prefix : Settings.prefixes){
			if(p.hasPermission(prefix.permission)){
				String format = reFormat(prefix.format, p);
				event.setFormat(format + event.getMessage());
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		Entity e = event.getEntity();
		if(e.getLastDamageCause() instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent cause = (EntityDamageByEntityEvent) e.getLastDamageCause();
			if(cause.getDamager() instanceof Player){
				Player p = (Player) cause.getDamager();
				PlayerData pd = profiles.get(p);
				for(EntityExp ee : Settings.ees){
					if(ee.isMyThicMob){
						BukkitAPIHelper api = new BukkitAPIHelper();
						if(api.isMythicMob(e)
								&& api.getMythicMobInstance(e).getType()
								.getInternalName().equalsIgnoreCase(ee.type)){
							pd.gainExp(ee.exp);
						}
					}else{
						if(e.getType().toString().equalsIgnoreCase(ee.type)){
							pd.gainExp(ee.exp);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event){
		Block b = event.getBlock();
		Player p = event.getPlayer();
		PlayerData pd = profiles.get(p);
		if(pd==null) return;
		for(BlockExp be : Settings.bes){
			if(b.getType().toString().equalsIgnoreCase(be.type)){
				pd.gainExp(be.exp);
			}
		}
	}

	public static String reFormat(String s, Player p){
		PlayerData pd = profiles.get(p);
		if(pd == null) return s;
		s = s.replace("{player}", p.getName());
		s = s.replace("{level}", ""+ pd.level);
		s = s.replace("{levelpoint}", ""+pd.exp);
		s = s.replace("{levellimit}", ""+getLimitExp(p));
		return s;
	}


	public static double getLimitExp(Player p){
		PlayerData pd = profiles.get(p);
		if(pd==null) return 9999999;
		return Settings.leveling.get(pd.level);
	}
}
