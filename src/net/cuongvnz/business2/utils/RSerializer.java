package net.cuongvnz.business2.utils;

import com.google.common.io.BaseEncoding;
import net.cuongvnz.business2.PlayerLevel;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.regex.Pattern;

public class RSerializer {

    private static final String LOCATION_DIVIDER = "||LOC||";

    public static Object[] deserializeArray(String s) {
        ByteArrayInputStream in = new ByteArrayInputStream(Base64.decodeBase64(s.getBytes()));
        try {
            return (Object[]) new ObjectInputStream(in).readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object deserializeObject(String s) {
        ByteArrayInputStream in = new ByteArrayInputStream(Base64.decodeBase64(s.getBytes()));
        try {
            return new ObjectInputStream(in).readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String serialize(Object o) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(out).writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(Base64.encodeBase64(out.toByteArray()));
    }

    public static Location deserializeLocation(String s) {
        Location def = PlayerLevel.plugin.getServer().getWorlds().get(0).getSpawnLocation();
        if (s.length() == 0)
            return def;
        String[] data = s.split(Pattern.quote(LOCATION_DIVIDER));
        if (data.length == 0)
            return def;
        try {
            double x = Double.parseDouble(data[0]);
            double y = Double.parseDouble(data[1]);
            if (y < 1)
                y = 1;
            double z = Double.parseDouble(data[2]);
            float yaw = Float.parseFloat(data[3]);
            float pitch = Float.parseFloat(data[4]);
            World w = PlayerLevel.plugin.getServer().getWorld(data[5]);
            if (w == null) {
                PlayerLevel.plugin.getServer().getWorlds().get(0).getSpawnLocation();
            }
            return new Location(w, x, y, z, yaw, pitch);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Corrupted location save: " + s);
        }
        return def;
    }

    public static String serializeLocation(Location loc) {
        StringBuilder sb = new StringBuilder();
        sb.append(loc.getX());
        sb.append(LOCATION_DIVIDER);
        if (loc.getY() < 1)
            sb.append("1.0");
        else
            sb.append(loc.getY());
        sb.append(LOCATION_DIVIDER);
        sb.append(loc.getZ());
        sb.append(LOCATION_DIVIDER);
        sb.append(loc.getYaw());
        sb.append(LOCATION_DIVIDER);
        sb.append(loc.getPitch());
        sb.append(LOCATION_DIVIDER);
        sb.append(loc.getWorld().getName());
        return sb.toString();
    }

    public static String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null)
            return "null";

        ByteArrayOutputStream outputStream = null;
        NBTTagCompound tagComp = new NBTTagCompound();
        net.minecraft.server.v1_12_R1.ItemStack copy = CraftItemStack.asNMSCopy(itemStack);
        if (copy == null)
            return "null";
        copy.save(tagComp);
        outputStream = new ByteArrayOutputStream();
        try {
            NBTCompressedStreamTools.a(tagComp, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = BaseEncoding.base64().encode(outputStream.toByteArray());
        if (str.contains("@")) {
            try {
                RMessages.announce("Error code 109: Item serialization @. Please report this to ChinnSu.");
                throw new Exception("ItemStack serialization contains @ symbol. " + itemStack);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        }
        return str;
    }

    public static ItemStack deserializeItemStack(String itemStackString) {
        if (itemStackString == null || itemStackString.length() == 0 || itemStackString.equals("null"))
            return null;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(BaseEncoding.base64().decode(itemStackString));
            NBTTagCompound tagComp;
        try {
            tagComp = NBTCompressedStreamTools.a(inputStream);
            net.minecraft.server.v1_12_R1.ItemStack item = new net.minecraft.server.v1_12_R1.ItemStack(tagComp);
            ItemStack bukkitItem = CraftItemStack.asBukkitCopy(item);
            return bukkitItem;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
