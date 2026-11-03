package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import java.util.*;
public class RankManager {
    private final FrizProject pl;
    public RankManager(FrizProject pl){ this.pl=pl; }
    public Rank rank(Player p){ return pl.pdm().data(p).rank(); }
    public StaffRole staff(Player p){ return pl.pdm().data(p).staff(); }
    public boolean isStaff(Player p){ return staff(p)!=StaffRole.NONE; }
    public void applyTag(Player p){
        Rank r=rank(p); StaffRole sr=staff(p);
        String tag = sr!=StaffRole.NONE? TextFormat.colorize(sr.color+"["+sr.name+"] "+r.color+r.name) : TextFormat.colorize(r.color+r.name);
        p.setNameTag(tag);
        if(pl.disg()!=null && pl.disg().hasDisguise(p)) pl.disg().apply(p);
    }
}