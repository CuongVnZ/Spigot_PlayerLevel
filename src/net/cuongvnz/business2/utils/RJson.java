package net.cuongvnz.business2.utils;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class RJson {

    public static void playOut(Player p, String JsonText) {
        EntityPlayer entity = ((CraftPlayer) p).getHandle();
        IChatBaseComponent component = ChatSerializer.a(JsonText);
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(component);
        entity.playerConnection.sendPacket(packetPlayOutChat);
    }

}