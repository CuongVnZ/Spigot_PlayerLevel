package net.cuongvnz.business2.commands.general;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.cuongvnz.business2.commands.AbstractCommand;

public class PlayerLevelCommand extends AbstractCommand {

    public PlayerLevelCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Hello motherfucker");
    }
    
    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

	@Override
	public void executePlayer(Player p, String[] args) {
        try{
            switch(args[0]){
                case "setlevel":
                    break;
                case "2":
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
