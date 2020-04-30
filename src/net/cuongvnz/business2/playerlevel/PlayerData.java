package net.cuongvnz.business2.playerlevel;

import net.cuongvnz.business2.Settings;
import org.bukkit.entity.Player;

public class PlayerData {

    public Player p;
    public int level;
    public double exp;

    public PlayerData(Player p){
        this.p = p;
    }

    public void gainExp(double amount){
        if(level >= Settings.MAX_LEVEL) return;
        exp+=amount;
        if(exp >= PlayerLevelManager.getLimitExp(p)){
            level+=1;
            p.sendMessage("Level Up");
        }
    }

}
