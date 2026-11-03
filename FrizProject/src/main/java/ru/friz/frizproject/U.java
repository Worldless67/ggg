package ru.friz.frizproject;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import java.util.*;
public class U {
    public static String c(String s){ return TextFormat.colorize(s); }
    public static Item item(int id,int meta,int count,String name,String frizId,String lore){
        Item it=Item.get(id,meta,count);
        if(name!=null) it.setCustomName(c(name));
        if(lore!=null) it.setLore(c(lore).split("\\n"));
        if(frizId!=null){ CompoundTag tag=it.getNamedTag(); if(tag==null) tag=new CompoundTag(); tag.putString("friz_id",frizId); it.setNamedTag(tag); }
        return it;
    }
    public static Item ench(Item it, int enchId, int lvl){
        try{ it.addEnchantment(Enchantment.getEnchantment(enchId).setLevel(lvl)); }catch(Throwable ignored){}
        return it;
    }
    public static String frizId(Item it){
        CompoundTag tag=it.getNamedTag();
        return tag!=null?tag.getString("friz_id"):"";
    }
    public static String time(long sec){
        long h=sec/3600,m=(sec%3600)/60,s=sec%60;
        if(h>0) return h+" ч "+m+" мин";
        if(m>0) return m+" мин "+s+" с";
        return s+" с";
    }
    public static String fmtMoney(double v){ return String.format("%.1f",v); }
}