package ru.friz.frizproject;
import cn.nukkit.Player;
import java.util.*;
public class EconomyManager {
    private final FrizProject pl;
    private final Map<UUID,List<String>> history=new HashMap<>();
    public EconomyManager(FrizProject pl){ this.pl=pl; }
    private PlayerData d(Player p){ return pl.pdm().data(p); }
    public double get(Player p){ return d(p).money(); }
    public void add(Player p, double v){ d(p).setMoney(d(p).money()+v); note(p, "Получено +"+U.fmtMoney(v)); }
    public boolean has(Player p, double v){ return d(p).money()>=v; }
    public boolean take(Player p, double v){
        if(!has(p,v)) return false;
        d(p).setMoney(d(p).money()-v); note(p,"Списано -"+U.fmtMoney(v));
        return true;
    }
    public int fk(Player p){ return d(p).fk(); }
    public void addFk(Player p,int v){ d(p).setFk(d(p).fk()+v); note(p,"FK +"+v); }
    public boolean takeFk(Player p,int v){
        if(d(p).fk()<v) return false;
        d(p).setFk(d(p).fk()-v); note(p,"FK -"+v);
        return true;
    }
    private void note(Player p, String s){
        List<String> h=history.computeIfAbsent(p.getUniqueId(),k->new ArrayList<>());
        h.add(s); if(h.size()>20) h.remove(0);
    }
    public List<String> noteHistory(Player p){ return history.getOrDefault(p.getUniqueId(),new ArrayList<>()); }
    public List<Map.Entry<String,Double>> top(int limit){
        List<Map.Entry<String,Double>> l=new ArrayList<>();
        for(PlayerData d: pl.pdm().all().values()) l.add(new AbstractMap.SimpleEntry<>(d.name().isEmpty()?"-":d.name(), d.money()));
        l.sort((a,b)->Double.compare(b.getValue(),a.getValue()));
        return l.subList(0,Math.min(limit,l.size()));
    }
    public List<Map.Entry<String,Integer>> fkTop(int limit){
        List<Map.Entry<String,Integer>> l=new ArrayList<>();
        for(PlayerData d: pl.pdm().all().values()) l.add(new AbstractMap.SimpleEntry<>(d.name().isEmpty()?"-":d.name(), d.fk()));
        l.sort((a,b)->Integer.compare(b.getValue(),a.getValue()));
        return l.subList(0,Math.min(limit,l.size()));
    }
}