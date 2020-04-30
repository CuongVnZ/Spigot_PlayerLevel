package net.cuongvnz.business2;

import net.cuongvnz.business2.playerlevel.ChatPrefix;
import net.cuongvnz.business2.playerlevel.PlayerLevelManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings {

    public static ArrayList<ChatPrefix> prefixes = new ArrayList<>();
    public static HashMap<Integer, Double> leveling = new HashMap<>();

    public static int MAX_LEVEL = 50;

    public static void reload(){
        PlayerLevel plugin = PlayerLevel.plugin;
        FileConfiguration cfg = plugin.getConfig();

        MAX_LEVEL = cfg.getInt("SETTINGS.MAX_LEVEL");

        prefixes.clear();
        for(String key : cfg.getConfigurationSection("CHAT_FORMAT.PREFIXES").getKeys(false)){
            String format = cfg.getString("CHAT_FORMAT.PREFIXES." + key + ".FORMAT");
            String perm = cfg.getString("CHAT_FORMAT.PREFIXES." + key + ".PERM");
            ChatPrefix prefix = new ChatPrefix(format, perm);
            prefixes.add(prefix);
        }

        leveling.clear();;
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        for(int i = 1; i <= MAX_LEVEL; i++){
            double exp = 10;
            try{
                exp = Double.parseDouble(engine.eval(cfg.getString("SETTINGS.LEVEL_SCALE").replace("{level}", ""+i)).toString());
            }catch (ScriptException e){
                e.printStackTrace();
            }
            leveling.put(i, exp);
        }

        PlayerLevelManager.reload();
    }
}
