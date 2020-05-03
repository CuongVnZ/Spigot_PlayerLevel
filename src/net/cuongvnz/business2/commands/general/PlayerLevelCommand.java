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
        try{
            if(args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);
                PlayerData pd = PlayerLevelManager.profiles.get(target);
                switch (args[0]) {
                    case "set":
                        pd.level = Integer.parseInt(args[2]);
                        sender.sendMessage(Settings.MSG_PREFIX + "Done");
                        break;
                    case "give":
                        double exp = Double.parseDouble(args[2]);
                        pd.gainExp(exp);
                        sender.sendMessage(Settings.MSG_PREFIX + "Done");
                        break;
                }
            }else if(args.length == 1){
                if(args[0].equalsIgnoreCase("reload")){
                    plugin.reloadConfig();
                    Settings.reload();
                    sender.sendMessage(Settings.MSG_RELOADED);
                }
            }
        }catch(Exception e){
            help(sender);
        }
    }
    
    @Override
    public void executeConsole(CommandSender sender, String[] args) {

    }

	@Override
	public void executePlayer(Player p, String[] args) {

	}

	public void help(CommandSender p){
        p.sendMessage("--------------");
        p.sendMessage("/playerlevel set <name> <amount>");
        p.sendMessage("/playerlevel give <name> <amount>");
        p.sendMessage("/playerlevel reload");
        p.sendMessage("--------------");
    }

}
