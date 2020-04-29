package net.cuongvnz.business2.commands;

import java.util.Arrays;

import net.cuongvnz.business2.PlayerLevel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class AbstractCommand extends Command implements CommandExecutor, Listener {

    public static PlayerLevel plugin;
    protected String requiredPerm = null;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command == null)
            return false;
        if (sender == null)
            return false;
        if (!command.getName().equalsIgnoreCase(getName()))
            return false;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (requiredPerm != null && !p.hasPermission(requiredPerm)) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
                return false;
            }
            execute(sender, args);
            executePlayer(p, args);
        } else if (sender instanceof ConsoleCommandSender) {
            execute(sender, args);
            executeConsole(sender, args);
        }
        System.out.println("Executing /" + command.getName() + " for " + sender.getName() + " with args " + Arrays.toString(args));
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return this.onCommand(sender, this, commandLabel, args);
    }

    /*
     * Run by both player and console executions.
     */
    public abstract void execute(CommandSender sender, String[] args);

    /*
     * Special execution for player command.
     */
    public abstract void executePlayer(Player p, String[] args);

    /*
     * Special execution for console command.
     */
    public abstract void executeConsole(CommandSender sender, String[] args);

    public AbstractCommand(String... commandNames) {
        super(commandNames[0]);
        if (commandNames.length > 1)
            for (int k = 1; k < commandNames.length; k++)
                getAliases().add(commandNames[k]);
    }
}
