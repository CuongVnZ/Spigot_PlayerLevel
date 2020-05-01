package net.cuongvnz.business2.commands.general;

import net.cuongvnz.business2.Settings;
import net.cuongvnz.business2.playerlevel.PlayerData;
import net.cuongvnz.business2.playerlevel.PlayerLevelManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.cuongvnz.business2.commands.AbstractCommand;

public class PlayerLevelCommand extends AbstractCommand {

    public PlayerLevelCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }
    
    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

	@Override
	public void executePlayer(Player p, String[] args) {
        try{
            Player target = Bukkit.getPlayer(args[1]);
            PlayerData pd = PlayerLevelManager.profiles.get(p);
            switch(args[0]){
                case "set":
                    int level = Integer.parseInt(args[2]);
                    pd.level = level;
                    p.sendMessage(Settings.MSG_PREFIX + "Done");
                    break;
                case "give":
                    double exp = Double.parseDouble(args[2]);
                    pd.gainExp(exp);
                    p.sendMessage(Settings.MSG_PREFIX + "Done");
                    break;
            }
        }catch(Exception e){
            help(p);
        }
	}

	public void help(Player p){
        p.sendMessage("--------------");
        p.sendMessage("");
        p.sendMessage("");
        p.sendMessage("");
        p.sendMessage("");
    }

}
