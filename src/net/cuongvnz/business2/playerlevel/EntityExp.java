package net.cuongvnz.business2.playerlevel;

import org.bukkit.entity.EntityType;

public class EntityExp {

    public String type;
    public double exp;
    public boolean isMyThicMob;

    public EntityExp(String type, double exp, boolean isMM){
        this.type = type;
        this.exp = exp;
        this.isMyThicMob = isMM;
    }

}
