package ru.friz.frizproject;
import cn.nukkit.item.Item;
import java.util.*;
import static ru.friz.frizproject.Ids.*;
public class ItemCatalog {
    // ---- Сферы (9 + Фин под уточнение) ----
    public static String[] SPHERES={"Могущества","Карающая","Поглощения","Крушения","Ярости","Фонтана","Сердца","Хаоса","Сущности","Фин(уточняется)"};
    public static String sphereId(int i){ return "sphere_"+i; }
    public static Item sphere(int i){
        String nm="Сфера "+SPHERES[Math.min(i,SPHERES.length-1)];
        String lore="&7Мистическая сфера.\\n&7Назначение: &f(уточняется Создателем)";
        return U.item(288,0,1,nm, sphereId(i), lore);
    }
    // ---- Талисманы ----
    private static final String[] TAL={"Скорости","Силы","Живучести","Стойкости","Ночного зрения","Огня","Дыхания","Прыгучести","Вампира","Шахтёра"};
    public static String talismanId(int i){ return "talisman_"+i; }
    public static Item talisman(int i){
        String nm="Талисман "+TAL[i%TAL.length];
        return U.item(399,0,1,nm,talismanId(i),"&7Пассивный талисман.\\n&7Держите в инвентаре.");
    }
    // ---- Оружие ----
    public static Item swordVampire(){ Item it=U.item(276,0,1,"&cКлинок Вампира","w_vampire",null); return U.ench(it,SHARPNESS,4); }
    public static Item swordPoison(){ Item it=U.item(276,0,1,"&aКлинок Змея","w_poison",null); return U.ench(it,SHARPNESS,3); }
    public static Item bowPower(){ Item it=U.item(261,0,1,"&5Лук Могущества","w_power",null); U.ench(it,POWER,4); U.ench(it,FLAME,1); return it; }
    public static Item trident(){ return U.item(571,0,1,"&3Трезубец Хранителя","w_trident",null); }
    public static Item crossbow(){ return U.item(471,0,1,"&6Арбалет","w_crossbow",null); }
    // ---- Броня ----
    public static Item[] hranArmor(){
        return new Item[]{ suit(311,"&bШлем Хранителя","a_hran_h"), suit(312,"&bКираса Хранителя","a_hran_c"), suit(313,"&bПоножи Хранителя","a_hran_l"), suit(314,"&bБотинки Хранителя","a_hran_b") };
    }
    public static Item[] vlastArmor(){
        return new Item[]{ suit(311,"&5Шлем Властелина","a_vlast_h"), suit(312,"&5Кираса Властелина","a_vlast_c"), suit(313,"&5Поножи Властелина","a_vlast_l"), suit(314,"&5Ботинки Властелина","a_vlast_b") };
    }
    private static Item suit(int id,String nm,String fid){ Item it=U.item(id,0,1,nm,fid,null); U.ench(it,PROTECTION,4); U.ench(it,UNBREAKING,3); return it; }
    // ---- Инструменты ----
    public static Item axeLumber(){ Item it=U.item(279,0,1,"&6Топор Дровосека","t_lumber",null); return U.ench(it,EFFICIENCY,5); }
    public static Item pickAuto(){ Item it=U.item(278,0,1,"&bКирка Автоплавки","t_auto",null); U.ench(it,EFFICIENCY,4); U.ench(it,FORTUNE,3); return it; }
    public static Item pickBull(){ Item it=U.item(278,0,1,"&6Кирка Бульдозер","t_bull",null); return U.ench(it,EFFICIENCY,5); }
    public static Item shovelExp(){ Item it=U.item(277,0,1,"&aЛопата Опытного","t_exp",null); return U.ench(it,EFFICIENCY,4); }
    public static Item magnet(){ return U.item(345,0,1,"&3Магнит","t_magnet","&7Притягивает дроп рядом."); }
    // ---- Расходники ----
    public static Item shulkerVacuum(){ return U.item(500,0,1,"&dШалкер-пылесос","x_vacuum","&7Собирает дроп вокруг себя."); }
    public static Item sphereCrate(){ return U.item(288,0,1,"&6Сфера кейса","x_sphere_crate",null); }
    public static Item healApple(){ return U.item(322,0,8,"&cЗолотое яблоко",null,null); }
    // ---- Зелья ----
    public static final Map<String,Integer> POTIONS=new LinkedHashMap<>();
    static { POTIONS.put("Скорость",8206); POTIONS.put("Сила",8201); POTIONS.put("Регенерация",8197); POTIONS.put("Огнестойкость",8195); POTIONS.put("Ночное зрение",8198); POTIONS.put("Вода",437); POTIONS.put("Прыгучесть",8203); POTIONS.put("Замедление",8202); POTIONS.put("Ускорение разбивания",8205); POTIONS.put("Невидимость",8206); POTIONS.put("Сопротивление",8212); POTIONS.put("Урон",8204); POTIONS.put("Прыгучесть II",8235); }
    public static List<Item> allPotions(){
        List<Item> l=new ArrayList<>();
        for(Map.Entry<String,Integer> e: POTIONS.entrySet()){
            if(e.getValue()==437) l.add(U.item(437,0,1,"&bВода",null,null));
            else l.add(U.item(438,e.getValue()-8200,1,"&5Зелье "+e.getKey(),null,null));
        }
        return l;
    }
}