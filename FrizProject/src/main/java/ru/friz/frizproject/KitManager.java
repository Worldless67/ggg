package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import java.util.*;
public class KitManager {
    private final FrizProject pl;
    public KitManager(FrizProject pl){ this.pl=pl; }
    public static final String[] NORMAL_KITS={"Кит СТАРТ","Кит МАГИСТР","Кит ВЛАСТЕЛИН","Кит ВОИН","Кит СТРАННИК","Кит ЛОРД","Кит ЛЕГЕНДА","Кит ПРИНЦ","Кит ХРАНИТЕЛЬ"};
    public static final String[] MEDIA_KITS={"Медия Кит 1","Медия Кит 2"};
    public String requiredRank(String kit){
        switch(kit){
            case "Кит МАГИСТР": return "Магистр";
            case "Кит ВЛАСТЕЛИН": return "Властелин";
            case "Кит ВОИН": return "Воин";
            case "Кит СТРАННИК": return "Странник";
            case "Кит ЛОРД": return "Лорд";
            case "Кит ЛЕГЕНДА": return "Легенда";
            case "Кит ПРИНЦ": return "Принц";
            case "Кит ХРАНИТЕЛЬ": return "Хранитель";
            default: return "Игрок";
        }
    }
    public long cooldownSec(String kit){
        String h=pl.getConfig().getString("kits."+kit.toLowerCase().replace("кит ","").trim()+"-hours","12");
        try{ return Long.parseLong(h)*3600; }catch(Exception e){}
        if(kit.contains("МАГИСТР")) return pl.getConfig().getLong("kits.magister-hours",48)*3600;
        if(kit.contains("ВЛАСТЕЛИН")) return pl.getConfig().getLong("kits.vlastelin-hours",24)*3600;
        return pl.getConfig().getLong("kits.start-hours",12)*3600;
    }
    public long remaining(Player p, String kit){
        long cd=pl.pdm().data(p).kitCooldowns().getOrDefault(kit.toLowerCase(),0L);
        long left=cd-(System.currentTimeMillis()/1000);
        return Math.max(0,left);
    }
    public boolean canTake(Player p, String kit){
        Rank need=Rank.byName(requiredRank(kit));
        return pl.rm().rank(p).ordinal()>=need.ordinal() || need==Rank.PLAYER;
    }
    public Item[] contents(String kit){
        switch(kit){
            case "Кит СТАРТ":
                return new Item[]{
                    U.item(276,0,1,"&fМеч новичка","kit_start_sword",null),
                    U.item(278,0,1,"&fКирка новичка","kit_start_pick",null),
                    U.item(306,0,1,"&fШлем",null,null), U.item(307,0,1,"&fКираса",null,null),
                    U.item(308,0,1,"&fПоножи",null,null), U.item(309,0,1,"&fБотинки",null,null),
                    U.item(320,0,16,"&fЖареная говядина",null,null), U.item(4,0,32,"&fФакелы",null,null)
                };
            case "Кит МАГИСТР":
                return new Item[]{
                    ItemCatalog.swordVampire(), ItemCatalog.bowPower(), ItemCatalog.trident(), ItemCatalog.crossbow(),
                    U.item(262,0,64,"&fСтрелы",null,null), U.item(262,0,64,"&fСтрелы",null,null),
                    ItemCatalog.hranArmor()[0], ItemCatalog.hranArmor()[1], ItemCatalog.hranArmor()[2], ItemCatalog.hranArmor()[3],
                    ItemCatalog.axeLumber(), ItemCatalog.pickAuto(), ItemCatalog.shovelExp(),
                    ItemCatalog.healApple(), ItemCatalog.healApple(),
                    U.item(49,0,64,"&fОбсидиан",null,null), U.item(7,0,32,"&fПортал-рамки",null,null),
                    U.item(8,0,16,"&fВода",null,null), U.item(4,0,64,"&fФакелы",null,null),
                    ItemCatalog.talisman(3), ItemCatalog.allPotions().get(0), ItemCatalog.allPotions().get(1)
                };
            case "Кит ВЛАСТЕЛИН":
                return new Item[]{
                    ItemCatalog.hranArmor()[0], ItemCatalog.hranArmor()[1], ItemCatalog.hranArmor()[2], ItemCatalog.hranArmor()[3],
                    ItemCatalog.swordVampire(), U.item(262,0,32,"&fСтрелы",null,null),
                    U.item(49,0,16,"&fОбсидиан",null,null), U.item(322,0,4,"&cЗолотые яблоки",null,null)
                };
            default:
                // п.22 ТЗ: состав не выдумываем, помечаем как уточняемый
                return new Item[]{ U.item(288,0,1,"&6Сфера &7(состав уточняется)","kit_placeholder",null) };
        }
    }
    public String give(Player p, String kit){
        if(!canTake(p,kit)) return "Нужна привилегия: "+requiredRank(kit);
        if(remaining(p,kit)>0) return "Подождите "+U.time(remaining(p,kit));
        pl.stash().give(p, contents(kit));
        long cdSec=cooldownSec(kit);
        if(cdSec>0) pl.pdm().data(p).kitCooldowns().put(kit.toLowerCase(), System.currentTimeMillis()/1000+cdSec);
        return null;
    }
}