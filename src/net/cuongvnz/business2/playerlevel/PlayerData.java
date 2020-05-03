package net.cuongvnz.business2.playerlevel;

import net.cuongvnz.business2.Settings;
import org.bukkit.Bukkit;
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
        String gainExp = Settings.MSG_GAIN_EXP;
        gainExp = gainExp.replace("%amount%", amount+"");
        //p.sendMessage(gainExp);
        double limit = PlayerLevelManager.getLimitExp(p);
        if(exp >= limit){
            exp = exp-limit;
            level+=1;
            String lvUp = Settings.MSG_LEVEL_UP;
            lvUp = lvUp.replace("%old_level%", (level-1)+"");
            lvUp = lvUp.replace("%new_level%", level+"");
            p.sendMessage(Settings.MSG_PREFIX + lvUp);
            for(String cmd : Settings.lvlUp_commands){
                cmd = PlayerLevelManager.reFormat(cmd, p);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
            for(String cmd : Settings.lvl_commands.get(level)){
                cmd = PlayerLevelManager.reFormat(cmd, p);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }
    }

}
