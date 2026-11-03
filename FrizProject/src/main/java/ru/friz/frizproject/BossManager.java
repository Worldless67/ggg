package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import java.util.*;
public class BossManager implements Listener {
    private final FrizProject pl;
    private final Map<Long,UUID> lastHit=new HashMap<>();
    private final Map<String,Integer> defaultHp=new LinkedHashMap<>();
    public BossManager(FrizProject pl){
        this.pl=pl;
        defaultHp.put("guardian",200); defaultHp.put("leviathan",400); defaultHp.put("tyrant",800); defaultHp.put("demon",1200);
    }
    public boolean spawn(Player p, String type){
        String entity;
        switch(type){
            case "guardian": entity="IronGolem"; break;
            case "leviathan": entity="Slime"; break;
            case "tyrant": entity="Zombie"; break;
            case "demon": entity="Blaze"; break;
            default: p.sendMessage(U.c("&7Типы: guardian, leviathan, tyrant, demon")); return false;
        }
        Position pos=p.clone();
        Entity e=Entity.createEntity(entity, pos.add(0,0,2));
        if(e==null){ p.sendMessage(U.c("&cНе удалось создать босса.")); return false; }
        int hp=defaultHp.get(type);
        e.setMaxHealth(hp); e.setHealth(hp);
        e.setNameTag(TextFormat.colorize("&6Босс "+type.toUpperCase()+" &7["+hp+" HP]"));
        e.setNameTagVisible(true);
        e.spawnToAll();
        p.sendMessage(U.c("&aБосс &e"+type+"&a заспавнен с &f"+hp+" HP&a. Награду получит последний ударивший."));
        return true;
    }
    public void setDefaultHp(String type,int hp){ defaultHp.put(type,hp); }
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player) return;
        if(e.getDamager() instanceof Player) lastHit.put(e.getEntity().getId(), ((Player)e.getDamager()).getUniqueId());
    }
    @EventHandler
    public void onDeath(EntityDeathEvent e){
        UUID killer=lastHit.remove(e.getEntity().getId());
        if(killer==null) return;
        Player k=pl.getServer().getPlayer(killer);
        if(k==null) return;
        double money=5000; int fk=50;
        pl.eco().add(k,money); pl.eco().addFk(k,fk);
        Item sph=ItemCatalog.sphere(new Random().nextInt(ItemCatalog.SPHERES.length-1));
        pl.stash().give(k, sph);
        pl.getServer().broadcastMessage(U.c("&6[Босс] &f"+k.getName()+" &aубил босса! Награда: &e"+U.fmtMoney(money)+" &aмонет, &e"+fk+" FK &aи сфера."));
    }
}