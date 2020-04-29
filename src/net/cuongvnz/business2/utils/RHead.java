package net.cuongvnz.business2.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RHead {
    public static final String PREFIX = "http://textures.minecraft.net/texture/";

    public static ItemStack getSkull(String url) {
        ItemStack head = new ItemStack(Material.valueOf(getSkullMaterial()), 1, (short) 3);
        if (url.isEmpty())
            return head;
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    @SuppressWarnings("deprecation")
	public static ItemStack getPlayerSkull(String playerName) {
        ItemStack head = new ItemStack(Material.valueOf(getSkullMaterial()), 1, (short) 3);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwner(playerName);
        head.setItemMeta(headMeta);
        return head;
    }

    private static String getSkullMaterial(){
        List<String> api = Arrays.asList("1.13", "1.14", "1.15");
        for(String s : api){
            if(Bukkit.getBukkitVersion().startsWith(s)){
                return "LEGACY_SKULL";
            }
        }
        return "SKULL_ITEM";
    }

}
