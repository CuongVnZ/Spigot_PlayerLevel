package net.cuongvnz.business2;

import net.cuongvnz.business2.playerlevel.BlockExp;
import net.cuongvnz.business2.playerlevel.ChatPrefix;
import net.cuongvnz.business2.playerlevel.EntityExp;
import net.cuongvnz.business2.playerlevel.PlayerLevelManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.EntityType;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Settings {

    public static ArrayList<ChatPrefix> prefixes = new ArrayList<>();
    public static HashMap<Integer, Double> leveling = new HashMap<>();
    public static ArrayList<EntityExp> ees = new ArrayList<>();
    public static ArrayList<BlockExp> bes = new ArrayList<>();

    public static HashMap<Integer, ArrayList<String>> lvl_commands = new HashMap<>();
    public static ArrayList<String> lvlUp_commands = new ArrayList<>();

    public static int MAX_LEVEL = 50;

    public static String PAPI_LEVEL = "level";
    public static String PAPI_LEVELPOINTS = "levelpoint";
    public static String PAPI_LEVELLIMIT = "levellimit";

    public static String MSG_PREFIX = "§c§l[PlayerLevel]";
    public static String MSG_LEVEL_UP = "§cLevel up %old_level% -> %new_level%";
    public static String MSG_GAIN_EXP = "Bạn đã nhận được %amount% exp";
    public static String MSG_RELOADED = "Đã reload lại file";

    public static void reload(){
        PlayerLevel plugin = PlayerLevel.plugin;
        FileConfiguration cfg = plugin.getConfig();

        MAX_LEVEL = cfg.getInt("SETTINGS.MAX_LEVEL");

        PAPI_LEVEL = cfg.getString("SETTINGS.PAPI.LEVEL");
        PAPI_LEVELPOINTS = cfg.getString("SETTINGS.PAPI.LEVEL_POINTS");
        PAPI_LEVELLIMIT = cfg.getString("SETTINGS.PAPI.LEVEL_LIMIT");

        MSG_PREFIX = cfg.getString("MESSAGES.PREFIX");
        MSG_LEVEL_UP = cfg.getString("MESSAGES.LEVEL_UP");
        MSG_GAIN_EXP = cfg.getString(("MESSAGES.GAIN_EXP"));

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
        for(String key : cfg.getConfigurationSection("SETTINGS.LEVEL_SPECIFIC").getKeys(false)){
            double exp = cfg.getDouble("SETTINGS.LEVEL_SPECIFIC."+key);
            leveling.replace(Integer.parseInt(key), exp);
        }

        ees.clear();
        for(String key : cfg.getConfigurationSection("GAIN_EXP.VANILLA_MOB").getKeys(false)){
            double exp = cfg.getDouble("GAIN_EXP.VANILLA_MOB."+key);
            EntityExp ee = new EntityExp(key, exp, false);
            ees.add(ee);
        }
        for(String key : cfg.getConfigurationSection("GAIN_EXP.MYTHIC_MOB").getKeys(false)){
            double exp = cfg.getDouble("GAIN_EXP.MYTHIC_MOB."+key);
            EntityExp ee = new EntityExp(key, exp, true);
            ees.add(ee);
        }

        bes.clear();
        for(String key : cfg.getConfigurationSection("GAIN_EXP.BREAK").getKeys(false)){
            double exp = cfg.getDouble("GAIN_EXP.BREAK."+key);
            BlockExp ee = new BlockExp(key, exp);
            bes.add(ee);
        }

        lvlUp_commands.clear();
        lvlUp_commands.addAll(cfg.getStringList("SETTINGS.LEVEL_UP_COMMAND"));

        lvl_commands.clear();
        for(String key : cfg.getConfigurationSection("SETTINGS.LEVEL_UP_COMMAND_SPECIFIC").getKeys(false)){
            ArrayList<String> cmds = new ArrayList<>(cfg.getStringList("SETTINGS.LEVEL_UP_COMMAND_SPECIFIC."+key));
            lvl_commands.put(Integer.parseInt(key), cmds);
        }

        PlayerLevelManager.reload();
    }
}
