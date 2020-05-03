package net.cuongvnz.business2.playerlevel.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.cuongvnz.business2.PlayerLevel;
import net.cuongvnz.business2.Settings;
import net.cuongvnz.business2.playerlevel.PlayerData;
import net.cuongvnz.business2.playerlevel.PlayerLevelManager;
import org.bukkit.entity.Player;

public class LevelHolder extends PlaceholderExpansion {

    private PlayerLevel plugin;

    public LevelHolder(PlayerLevel plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "playerlevel";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        PlayerData pd = PlayerLevelManager.profiles.get(player);

        if(pd==null) return "NaN";

        // %playerlevel_level%
        if(identifier.equals(Settings.PAPI_LEVEL)){
            return pd.level+"";
        }

        // %playerlevel_levelpoints%
        if(identifier.equals(Settings.PAPI_LEVELPOINTS)){
            return pd.exp+"";
        }

        // %playerlevel_levellimit%
        if(identifier.equals(Settings.PAPI_LEVELLIMIT)){
            return PlayerLevelManager.getLimitExp(player)+"";
        }

        // We return null if an invalid placeholder (f.e. %example_placeholder3%)
        // was provided
        return null;
    }
}
