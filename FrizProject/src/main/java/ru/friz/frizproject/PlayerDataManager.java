package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import java.io.File;
import java.util.*;
public class PlayerDataManager {
    private final FrizProject pl;
    private final Map<UUID,PlayerData> cache=new HashMap<>();
    private Config store;
    public PlayerDataManager(FrizProject pl){ this.pl=pl; }
    public void init(){
        store=new Config(new File(pl.getDataFolder(),"players.yml"), Config.YAML);
        for(String k: store.getKeys()) parse(k, (Map)store.get(k));
    }
    private void parse(String k, Map m){
        try{
            UUID id=UUID.fromString(k);
            PlayerData d=new PlayerData();
            d.setName((String)m.getOrDefault("name",""));
            d.setMoney(((Number)m.getOrDefault("money",0.0)).doubleValue());
            d.setFk(((Number)m.getOrDefault("fk",0)).intValue());
            d.setPlaytime(((Number)m.getOrDefault("playtime",0L)).longValue());
            d.setDailyStreak(((Number)m.getOrDefault("streak",0)).intValue());
            d.setLastDaily(((Number)m.getOrDefault("lastDaily",0L)).longValue());
            try{ d.setRank(Rank.byName((String)m.getOrDefault("rank","PLAYER"))); }catch(Throwable t){}
            try{ d.setStaff(StaffRole.byName((String)m.getOrDefault("staff","NONE"))); }catch(Throwable t){}
            d.setDisguiseNick((String)m.getOrDefault("disgNick",""));
            d.setDisguiseRank((String)m.getOrDefault("disgRank",""));
            d.setKills(((Number)m.getOrDefault("kills",0)).intValue());
            d.setDeaths(((Number)m.getOrDefault("deaths",0)).intValue());
            d.setBreaks(((Number)m.getOrDefault("breaks",0)).intValue());
            d.setPvp((Boolean)m.getOrDefault("pvp",true));
            if(m.get("cd") instanceof Map cm){ for(Object o: cm.entrySet()){ Map.Entry en=(Map.Entry)o; d.kitCooldowns().put((String)en.getKey(), ((Number)en.getValue()).longValue()); } }
            cache.put(id,d);
        }catch(Throwable ignored){}
    }
    public PlayerData data(Player p){
        PlayerData d=cache.get(p.getUniqueId());
        if(d==null){ d=new PlayerData(); d.setName(p.getName()); cache.put(p.getUniqueId(),d); }
        return d;
    }
    public PlayerData dataByName(String n){
        for(PlayerData d: cache.values()) if(d.name().equalsIgnoreCase(n)) return d;
        for(Map.Entry<UUID,PlayerData> e: cache.entrySet()) if(e.getValue().name().isEmpty()){
            Player p=pl.getServer().getPlayerExact(n);
            if(p!=null && e.getKey().equals(p.getUniqueId())) return e.getValue();
        }
        return null;
    }
    public void save(){
        Map<String,Object> out=new HashMap<>();
        for(Map.Entry<UUID,PlayerData> e: cache.entrySet()){
            PlayerData d=e.getValue();
            Map<String,Object> m=new HashMap<>();
            m.put("name",d.name()); m.put("money",d.money()); m.put("fk",d.fk());
            m.put("playtime",d.playtime()); m.put("streak",d.dailyStreak()); m.put("lastDaily",d.lastDaily());
            m.put("rank",d.rank().name()); m.put("staff",d.staff().name());
            m.put("disgNick",d.disguiseNick()); m.put("disgRank",d.disguiseRank());
            m.put("kills",d.kills()); m.put("deaths",d.deaths()); m.put("breaks",d.breaks()); m.put("pvp",d.pvp());
            if(!d.kitCooldowns().isEmpty()) m.put("cd",new HashMap<>(d.kitCooldowns()));
            out.put(e.getKey().toString(),m);
        }
        store.setAll(out); store.save();
    }
    public Map<UUID,PlayerData> all(){ return cache; }
}