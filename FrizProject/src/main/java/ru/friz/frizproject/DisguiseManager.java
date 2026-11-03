package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
public class DisguiseManager {
    private final FrizProject pl;
    public DisguiseManager(FrizProject pl){ this.pl=pl; }
    public boolean hasDisguise(Player p){
        return !pl.pdm().data(p).disguiseNick().isEmpty();
    }
    public void apply(Player p){
        PlayerData d=pl.pdm().data(p);
        if(d.disguiseNick().isEmpty()) return;
        String nick=TextFormat.colorize(d.disguiseNick());
        String rank=d.disguiseRank().isEmpty()? "" : TextFormat.colorize(d.disguiseRank())+" ";
        p.setNameTag(rank+nick);
        p.setDisplayName(rank+nick);
    }
    public void set(Player p, String nick, String rank){
        PlayerData d=pl.pdm().data(p);
        d.setDisguiseNick(nick==null?"":nick);
        d.setDisguiseRank(rank==null?"":rank);
        apply(p);
    }
    public void clear(Player p){
        set(p,null,null);
        pl.rm().applyTag(p);
    }
}