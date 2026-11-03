package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import java.util.*;
public class StaffCommands implements CommandExecutor {
    private final FrizProject pl;
    public StaffCommands(FrizProject pl){ this.pl=pl; }
    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] a){
        if(!(s instanceof Player)) return true;
        Player p=(Player)s; StaffRole r=pl.rm().staff(p);
        if(r==StaffRole.NONE){ p.sendMessage(U.c("&cНедостаточно прав")); return true; }
        switch(c.getName().toLowerCase()){
            case "mute": if(a.length<2) return true; pl.mute(p,a[0],a[1]); return true;
            case "unmute": if(a.length<1) return true; pl.unmute(p,a[0]); return true;
            case "kick": if(a.length<1) return true; pl.kick(p,a[0]); return true;
            case "ban": if(a.length<1) return true; pl.ban(p,a[0]); return true;
            case "unban": if(a.length<1) return true; pl.unban(p,a[0]); return true;
            case "reports": pl.listReports(p); return true;
            case "warps": pl.listWarps(p); return true;
            case "setwarp": if(a.length<1) return true; pl.setWarp(p,a[0]); return true;
            case "delwarp": if(a.length<1) return true; pl.delWarp(p,a[0]); return true;
            case "economy": case "eco": if(a.length>=3) pl.ecoAdmin(p,a); else p.sendMessage(U.c("&7/eco give|take <ник> <сумма>")); return true;
            default: return false;
        }
    }
}