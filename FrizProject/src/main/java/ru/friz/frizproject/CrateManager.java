package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import java.util.*;
public class CrateManager {
    private final FrizProject pl;
    private final Random rnd=new Random();
    public CrateManager(FrizProject pl){ this.pl=pl; }
    public Item key(String type){
        return U.item(131,0,1,"&6Ключ "+type,"crate_key_"+type,"&7Откройте кейс на спавне.");
    }
    public String open(Player p, String type){
        Item keyItem=U.item(131,0,1,"&6Ключ "+type,"crate_key_"+type,null);
        if(!p.getInventory().contains(keyItem)){
            // ищем по friz_id
            boolean found=false;
            for(Item it: p.getInventory().getContents().values())
                if(U.frizId(it).equals("crate_key_"+type)){ found=true; p.getInventory().removeItem(it); break; }
            if(!found) return "Нет ключа типа: "+type;
        } else p.getInventory().removeItem(keyItem);
        int roll=rnd(1,100);
        Item prize;
        if(roll<=5) prize=ItemCatalog.sphere(rnd(0,8));
        else if(roll<=25) prize=ItemCatalog.talisman(rnd(0,9));
        else if(roll<=60) prize=ItemCatalog.healApple();
        else prize=U.item(264,0,rnd(1,8),"&fАлмазы",null,null);
        pl.stash().give(p,prize);
        p.sendMessage(U.c("&aИз кейса выпало: &f"+prize.getName()));
        return null;
    }
}