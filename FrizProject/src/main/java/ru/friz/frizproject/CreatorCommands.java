package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
public class CreatorCommands implements CommandExecutor {
    private final FrizProject pl;
    public CreatorCommands(FrizProject pl){ this.pl=pl; }
    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] a){
        if(!(s instanceof Player)) return true;
        Player p=(Player)s;
        if(pl.rm().staff(p)!=StaffRole.TVOREC){ p.sendMessage(U.c("&cКоманда только для Творца")); return true; }
        switch(c.getName().toLowerCase()){
            case "give": if(a.length<3) return true; pl.creatorGive(p,a[0],a[1],a[2]); return true;
            case "giveall": if(a.length<2) return true; pl.creatorGiveAll(p,a[0],a[1]); return true;
            case "fk": if(a.length>=3&&a[0].equalsIgnoreCase("give")) pl.creatorFk(p,a[1],a[2]); return true;
            case "setrank": if(a.length<2) return true; pl.creatorSetRank(p,a[0],a[1]); return true;
            case "boss": if(a.length<2) return true; pl.bossCmd(p,a); return true;
            case "event": if(a.length<2) return true; pl.eventCmd(p,a); return true;
            case "npc": pl.npcRespawn(); return true;
            case "title": if(a.length<1) return true; pl.broadcast(String.join(" ",a)); return true;
            case "setspawn": pl.setSpawnHere(p); return true;
            case "rebuildspawn": pl.rebuildSpawn(); return true;
            default: return false;
        }
    }
}