package net.cuongvnz.business2.menus;

import net.cuongvnz.business2.AbstractManager;
import net.cuongvnz.business2.PlayerLevel;
import net.cuongvnz.business2.utils.RFormatter;
import net.cuongvnz.business2.utils.RMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MenuManager extends AbstractManager {

    //[player uuid -> [inventory name -> [item name -> runnable]]
    private static final HashMap<UUID, HashMap<String, HashMap<String, Runnable>>> invClickables = new HashMap<UUID, HashMap<String, HashMap<String, Runnable>>>();

    private static final HashMap<UUID, MenuGeneralRunnable> generalClickables = new HashMap<UUID, MenuGeneralRunnable>();

    public MenuManager(PlayerLevel plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {

    }

    private static String serializeForMenu(ItemStack item) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getType());
        sb.append('$');
        sb.append(item.getAmount());
        if (item.hasItemMeta()) {
            ItemMeta im = item.getItemMeta();
            if (im.hasDisplayName()) {
                sb.append('#');
                sb.append(im.getDisplayName());
            }
            if (im.hasLore()) {
                sb.append('#');
                sb.append(im.getLore().toString());
            }
            if (im.hasEnchants()) {
                sb.append('#');
                sb.append(im.getEnchants().toString());
            }
        }
        return sb.toString();
    }

    public static void registerListener(Player p, ItemStack item, Inventory inventory, String title, Runnable runnable) {
        if (!invClickables.containsKey(p.getUniqueId()))
            invClickables.put(p.getUniqueId(), new HashMap<String, HashMap<String, Runnable>>());
        HashMap<String, HashMap<String, Runnable>> inventories = invClickables.get(p.getUniqueId());
        if (!inventories.containsKey(title))
            inventories.put(title, new HashMap<String, Runnable>());
        HashMap<String, Runnable> thisInv = inventories.get(title);
        thisInv.put(serializeForMenu(item), runnable);
    }

    public static void registerGeneral(Player p, MenuGeneralRunnable r) {
        generalClickables.put(p.getUniqueId(), r);
    }

    public static void clear(UUID uuid) {
        if (invClickables.containsKey(uuid))
            invClickables.remove(uuid).clear();
        if (generalClickables.containsKey(uuid))
            generalClickables.remove(uuid);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();
        if (invClickables.containsKey(uuid)) {
            HashMap<String, HashMap<String, Runnable>> invs = invClickables.get(uuid);
            String invName = getInventoryName(event);
            if (invName != null && invs.containsKey(invName) && event.getCurrentItem() != null) {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).updateInventory();
                ItemStack item = event.getCurrentItem();
                String serialized = serializeForMenu(item);
                //                System.out.println("checking " + invs.get(invName) + " for " + serialized);
                if (invs.get(invName).containsKey(serialized)) {
                    Runnable r = invs.get(invName).get(serialized);
                    r.run();
                }
            }
        }
        Object nmsClass = getNMSClass("CraftInventoryPlayer");
        if (event.getView() != null
                && event.getView().getBottomInventory() != null
                && event.getView().getBottomInventory().getClass().isInstance(nmsClass)) {
            if (event.getRawSlot() >= event.getView().getTopInventory().getSize() && generalClickables.containsKey(uuid)) {
                Player p = (Player) event.getWhoClicked();
                generalClickables.get(uuid).onClick(p, event.getCurrentItem(), event.getSlot());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (invClickables.containsKey(uuid)) {
            HashMap<String, HashMap<String, Runnable>> invs = invClickables.get(uuid);
            String invName = getInventoryName(event);
            if (invName != null && invs.containsKey(invName)) {
                HashMap<String, Runnable> map = invs.remove(invName);
                map.clear();
                map = null;
            }
            generalClickables.remove(uuid);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();
        if (invClickables.containsKey(uuid)) {
            HashMap<String, HashMap<String, Runnable>> invs = invClickables.get(uuid);
            if (getInventoryName(event) != null
                    && invs.containsKey(getInventoryName(event))) {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).updateInventory();
            }
        }
    }

    /*
     * Format for info is:
     * {
     *   {row, col, Material.WHATEVER, ChatColor.WHITE + "first slot title", new Object[] {color, "description here", null, "", color2, "lol"}, new Runnable() {}}
     * }
     */
    public static Inventory createMenu(Player p, String title, int rows, Object[][] info) {
        Inventory inventory = Bukkit.createInventory(null, 9 * rows, title);
        modifyMenu(p, inventory, title, info);
        return inventory;
    }

    public static Inventory createMenu(Player p, String title, int rows, MenuItem[] info) {
        Inventory inventory = Bukkit.createInventory(null, 9 * rows, title);
        modifyMenu(p, inventory, title, info);
        return inventory;
    }

    public static Inventory createMenu(Player p, String title, int rows, List<MenuItem> info) {
        Inventory inventory = Bukkit.createInventory(null, 9 * rows, title);
        modifyMenu(p, inventory, title, info.toArray(new MenuItem[info.size()]));
        return inventory;
    }

    /*
     * Must be called AFTER openInventory for a new inv b/c of closing old inventories
     */
    public static void addMenuGeneralClick(Player p, MenuGeneralRunnable r) {
        MenuManager.registerGeneral(p, r);
    }

    public static Inventory modifyMenu(Player p, Inventory inventory, String title, MenuItem[] info) {
        for (MenuItem mi : info) {
            int row = mi.row;
            int col = mi.col;
            int slot = row * 9 + col;
            ItemStack item = mi.item;
            if (item == null)
                item = new ItemStack(Material.BARRIER);
            ItemMeta im = item.getItemMeta();
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            item.setItemMeta(im);
            inventory.setItem(slot, item);
            Runnable r = mi.runnable;
            if (r != null)
                MenuManager.registerListener(p, item, inventory, title, r);
        }
        return inventory;
    }

    @SuppressWarnings("unchecked")
    public static Inventory modifyMenu(Player p, Inventory inventory, String title, Object[][] info) {
        for (Object[] data : info) {
            int row = (int) data[0];
            int col = (int) data[1];
            int slot = row * 9 + col;
            ItemStack item;
            if (data[2] == null) {
                item = null;
                inventory.setItem(slot, new ItemStack(Material.BARRIER));
                continue;
            } else if (data[2] instanceof Material) {
                item = new ItemStack((Material) data[2]);
            } else if (data[2] instanceof ItemStack) {
                item = (ItemStack) data[2];
            } else {
                item = null;
                RMessages.announce(ChatColor.RED + "Error 100. Please report to ChinnSu or Dev!");
                return inventory;
            }
            ItemMeta im = item.getItemMeta();
            if(im != null) {
                im.setDisplayName(data[3].toString());
                if (data[4] instanceof Object[]) {
                    Object[] desc = (Object[]) data[4];
                    ArrayList<String> lore = new ArrayList<String>();
                    for (int k = 0; k < desc.length; k += 2) {
                        lore.addAll(RFormatter.stringToLore(desc[k + 1].toString(), desc[k] != null ? desc[k].toString() : ChatColor.GRAY));
                    }
                    im.setLore(lore);
                } else if (data[4] instanceof ArrayList) {
                    im.setLore((ArrayList<String>) data[4]);
                }
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                item.setItemMeta(im);
            }
            inventory.setItem(slot, item);
            Runnable r = (Runnable) data[5];
            if (r != null)
                MenuManager.registerListener(p, item, inventory, title, r);
        }
        return inventory;
    }

    public static Class<?> getNMSClass(String name) {
        String version = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + ".inventory." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getInventoryName(InventoryEvent e) {
        try {
            Object o = null;
            if(isLegacy()) {
                o = e.getInventory();
                Method m = o.getClass().getMethod("getName");
                m.setAccessible(true);
                String name =  (String) m.invoke(o);
                m.setAccessible(false);
                return name;
            }
            o = e.getView();
            Method m = e.getView().getClass().getMethod("getTitle");
            m.setAccessible(true);
            String name =  (String) m.invoke(o);
            m.setAccessible(false);
            return name;
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }

    private static boolean isLegacy() {
        if (Bukkit.getBukkitVersion().startsWith("1.13")) {
            return false;
        }
        if (Bukkit.getBukkitVersion().startsWith("1.14")) {
            return false;
        }
        if (Bukkit.getBukkitVersion().startsWith("1.15")) {
            return false;
        }
        return true;
    }

}
