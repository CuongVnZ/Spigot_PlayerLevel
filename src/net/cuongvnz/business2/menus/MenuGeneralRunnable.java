/*
 *	  ___  _   _                     ____________ _____
 *	 / _ \| | | |                    | ___ \ ___ \  __ \
 *	/ /_\ \ |_| |__   ___ _ __   __ _| |_/ / |_/ / |  \/
 *	|  _  | __| '_ \ / _ \ '_ \ / _` |    /|  __/| | __
 *	| | | | |_| | | |  __/ | | | (_| | |\ \| |   | |_\ \
 *	\_| |_/\__|_| |_|\___|_| |_|\__,_\_| \_\_|    \____/
 *
 */
package net.cuongvnz.business2.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class MenuGeneralRunnable<T> {

    @SuppressWarnings("unchecked")
    public void onClick(Player p, ItemStack item, int slot) {
        if (p != null && item != null && item.getType() != Material.AIR)
            execute(p, item, slot);
    }

    public abstract void execute(final Player p, ItemStack item, int slot);

}