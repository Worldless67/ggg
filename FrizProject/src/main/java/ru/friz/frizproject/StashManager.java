package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import java.io.File;
import java.util.*;
public class StashManager {
    private final FrizProject pl;
    private final Map<UUID,List<Item>> stash=new HashMap<>();
    public StashManager(FrizProject pl){ this.pl=pl; }
    public void give(Player p, Item... items){
        Map<Integer,Item> left=p.getInventory().addItem(items);
        List<Item> l=stash.computeIfAbsent(p.getUniqueId(),k->new ArrayList<>());
        for(Item i:left.values()) l.add(i);
        if(!l.isEmpty()) p.sendMessage(U.c("&aВ хранилище добавлено предметов: &e"+l.size()+"&a. Открой через &e/stash"));
    }
    public void open(Player p){
        List<Item> l=stash.getOrDefault(p.getUniqueId(),new ArrayList<>());
        if(l.isEmpty()){ p.sendMessage(U.c("&7Хранилище пусто.")); return; }
        p.sendMessage(U.c("&6Хранилище (&f"+l.size()+"&6):"));
        int shown=0;
        for(Item it: l){
            shown++;
            if(shown>10){ p.sendMessage(U.c("&7... и ещё "+(l.size()-shown+1))); break; }
            p.sendMessage(U.c("&f- "+it.getName()+" x"+it.getCount()));
        }
        p.sendMessage(U.c("&7Выдать всё в инвентарь: &e/stash take"));
    }
    public void take(Player p){
        List<Item> l=stash.remove(p.getUniqueId());
        if(l==null||l.isEmpty()){ p.sendMessage(U.c("&7Хранилище пусто.")); return; }
        List<Item> left=p.getInventory().addItem(l.toArray(new Item[0]));
        if(!left.isEmpty()){
            stash.computeIfAbsent(p.getUniqueId(),k->new ArrayList<>()).addAll(left);
            p.sendMessage(U.c("&aЧасть не влезла — вернулась в хранилище."));
        } else p.sendMessage(U.c("&aХранилище выдано."));
    }
    public void save(){
        Map<String,Object> out=new HashMap<>();
        for(Map.Entry<UUID,List<Item>> e: stash.entrySet()){
            List<String> desc=new ArrayList<>();
            for(Item i: e.getValue()) desc.add(i.getId()+":"+i.getMeta()+"x"+i.getCount());
            out.put(e.getKey().toString(),desc);
        }
        Config c=new Config(new File(pl.getDataFolder(),"stash.yml"),Config.YAML);
        c.setAll(out); c.save();
    }
}