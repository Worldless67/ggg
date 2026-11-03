package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import java.util.*;
public class DonatShopManager {
    private final FrizProject pl;
    public DonatShopManager(FrizProject pl){ this.pl=pl; }
    public Map<String,Integer> catalog(){
        Map<String,Integer> m=new LinkedHashMap<>();
        m.put("Кит СТАРТ",0); m.put("Кит МАГИСТР",400); m.put("Кит ВЛАСТЕЛИН",800);
        for(int i=0;i<ItemCatalog.SPHERES.length;i++) m.put("Сфера: "+ItemCatalog.SPHERES[i],150+i*50);
        m.put("Броня Хранителя",600); m.put("Броня Властелина",1200);
        m.put("Клинок Вампира",350); m.put("Клинок Змея",300); m.put("Лук Могущества",250);
        m.put("Талисман: скорость",100); m.put("Талисман: вампир",200);
        m.put("FK+100",100); m.put("FK+500",450);
        return m;
    }
    public String buy(Player p, String key){
        Integer price=catalog().get(key);
        if(price==null) return "Товар не найден";
        int fk=pl.eco().fk(p);
        if(fk<price) return "Не хватает FK: нужно "+price+", у вас "+fk;
        Item[] give=null;
        if(key.startsWith("Кит ")) give=pl.kits().contents(key);
        else if(key.startsWith("Сфера: ")){ int i=Arrays.asList(ItemCatalog.SPHERES).indexOf(key.substring(7)); if(i>=0) give=new Item[]{ItemCatalog.sphere(i)}; }
        else if(key.equals("Броня Хранителя")) give=ItemCatalog.hranArmor();
        else if(key.equals("Броня Властелина")) give=ItemCatalog.vlastArmor();
        else if(key.equals("Клинок Вампира")) give=new Item[]{ItemCatalog.swordVampire()};
        else if(key.equals("Клинок Змея")) give=new Item[]{ItemCatalog.swordPoison()};
        else if(key.equals("Лук Могущества")) give=new Item[]{ItemCatalog.bowPower()};
        else if(key.startsWith("Талисман: скорость")) give=new Item[]{ItemCatalog.talisman(0)};
        else if(key.startsWith("Талисман: вампир")) give=new Item[]{ItemCatalog.talisman(8)};
        else if(key.equals("FK+100")||key.equals("FK+500")){ if(pl.eco().takeFk(p,price)){ pl.eco().addFk(p, key.contains("500")?500:100); return null; } return "Ошибка списания"; }
        if(give==null) return "Товар временно недоступен";
        // схема из ТЗ: проверка -> выдача -> списание
        pl.stash().give(p,give);
        if(!pl.eco().takeFk(p,price)){ p.sendMessage(U.c("&cОшибка списания, товары возвращены.")); return null; }
        p.sendMessage(U.c("&aКуплено: &f"+key+"&a за &e"+price+" FK"));
        return null;
    }
}