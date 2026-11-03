package ru.friz.frizproject;
import java.util.*;
public class PlayerData {
    private String name=""; private double money=0; private int fk=0;
    private long playtimeTicks=0; private int dailyStreak=0; private long lastDaily=0;
    private final Map<String,Long> kitCooldowns=new HashMap<>();
    private Rank rank=Rank.PLAYER; private StaffRole staff=StaffRole.NONE;
    private String disguiseNick="", disguiseRank="";
    private int kills=0,deaths=0,breaks=0; private boolean pvp=true;
    public String name(){ return name; } public void setName(String v){ name=v; }
    public double money(){ return money; } public void setMoney(double v){ money=v; }
    public int fk(){ return fk; } public void setFk(int v){ fk=v; }
    public long playtime(){ return playtimeTicks; } public void addPlaytime(long t){ playtimeTicks+=t; }
    public int dailyStreak(){ return dailyStreak; } public void setDailyStreak(int v){ dailyStreak=v; }
    public long lastDaily(){ return lastDaily; } public void setLastDaily(long v){ lastDaily=v; }
    public Map<String,Long> kitCooldowns(){ return kitCooldowns; }
    public Rank rank(){ return rank; } public void setRank(Rank r){ rank=r; }
    public StaffRole staff(){ return staff; } public void setStaff(StaffRole r){ staff=r; }
    public String disguiseNick(){ return disguiseNick; } public void setDisguiseNick(String s){ disguiseNick=s; }
    public String disguiseRank(){ return disguiseRank; } public void setDisguiseRank(String s){ disguiseRank=s; }
    public int kills(){ return kills; } public void setKills(int v){ kills=v; }
    public int deaths(){ return deaths; } public void setDeaths(int v){ deaths=v; }
    public int breaks(){ return breaks; } public void addBreak(int v){ breaks+=v; }
    public boolean pvp(){ return pvp; } public void setPvp(boolean v){ pvp=v; }
}