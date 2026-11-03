package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import java.util.*;
public class PlayerCommands implements CommandExecutor {
    private final FrizProject pl;
    public PlayerCommands(FrizProject pl){ this.pl=pl; }
    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] a){
        if(!(s instanceof Player)){ s.sendMessage("Только для игроков"); return true; }
        Player p=(Player)s; String k=c.getName().toLowerCase();
        switch(k){
            case "spawn": p.teleport(pl.map().spawn()); return true;
            case "rtp": {
                int x=pl.map().cx+(int)(Math.random()*2000-1000), z=pl.map().cz+(int)(Math.random()*2000-1000);
                int y=pl.map().level().getHighestBlockAt(x,z)+1;
                p.teleport(new Position(x,y,z,pl.map().level()));
                p.sendMessage(U.c("&aТелепорт в случайную точку!")); return true;
            }
            case "tpa": if(a.length<1){ p.sendMessage(U.c("&7/tpa <ник>")); return true; } pl.tpaRequest(p,a[0]); return true;
            case "tpaccept": pl.tpaAccept(p); return true;
            case "tpdeny": pl.tpaDeny(p); return true;
            case "msg": case "m": if(a.length<2) return true; pl.pm(p,a[0],String.join(" ",Arrays.copyOfRange(a,1,a.length))); return true;
            case "r": if(a.length<1) return true; pl.pmReply(p,String.join(" ",a)); return true;
            case "ignore": if(a.length<1) return true; pl.toggleIgnore(p,a[0]); return true;
            case "pay": if(a.length<2){ p.sendMessage(U.c("&7/pay <ник> <сумма>")); return true; } pl.pay(p,a[0],a[1]); return true;
            case "money": case "balance": p.sendMessage(U.c("&fБаланс: &a"+U.fmtMoney(pl.eco().get(p))+" &7| FK: &e"+pl.eco().fk(p))); return true;
            case "daily": pl.daily(p); return true;
            case "kits": pl.kitForms().main(p); return true;
            case "kit": if(a.length<1){ p.sendMessage(U.c("&7/kit <название> или /kits")); return true; } { String err=pl.kits().give(p,String.join(" ",a)); p.sendMessage(U.c(err!=null?"&c"+err:"&aКит получен!")); } return true;
            case "sell": if(a.length>0&&a[0].equalsIgnoreCase("all")) pl.shop().sellAll(p); else pl.shop().sellHand(p); return true;
            case "shop": pl.donateShop().list(p); return true;
            case "donateshop": pl.donateShop().list(p); return true;
            case "donate": p.sendMessage(U.c("&7Донат-валюта FK. Баланс: &e"+pl.eco().fk(p)+" FK")); return true;
            case "auction": pl.auction().cmd(p,a); return true;
            case "auc": pl.auction().cmd(p,a); return true;
            case "stats": { PlayerData d=pl.pdm().data(p); p.sendMessage(U.c("&fСтатистика: &aубийств "+d.kills()+"&f, смертей "+d.deaths()+"&f, блоков "+d.breaks())); } return true;
            case "top": { List<Map.Entry<String,Double>> t=pl.eco().top(10); p.sendMessage(U.c("&6Топ богатейших:")); int i=1; for(Map.Entry<String,Double> e: t) p.sendMessage(U.c("&f"+i+". &e"+e.getKey()+" &7— "+U.fmtMoney(e.getValue()))); } return true;
            case "fk": if(a.length>0&&a[0].equalsIgnoreCase("top")){ p.sendMessage(U.c("&6Топ FK:")); int i=1; for(Map.Entry<String,Integer> e: pl.eco().fkTop(10)) p.sendMessage(U.c("&f"+i+". &e"+e.getKey()+" &7— "+e.getValue()+" FK")); } else p.sendMessage(U.c("&fВаш FK: &e"+pl.eco().fk(p))); return true;
            case "report": if(a.length<2){ p.sendMessage(U.c("&7/report <ник> <причина>")); return true; } pl.report(p,a[0],String.join(" ",Arrays.copyOfRange(a,1,a.length))); return true;
            case "home": pl.homeCmd(p,a); return true;
            case "warp": pl.warpCmd(p,a); return true;
            case "list": case "online": p.sendMessage(U.c("&fОнлайн: &e"+pl.getServer().getOnlinePlayers().size())); return true;
            case "ping": p.sendMessage(U.c("&fПинг: &e"+p.getPing())); return true;
            case "help": p.sendMessage(U.c("&6Основные команды:\n&f/spawn, /rtp, /tpa, /pay, /daily, /kits, /shop, /sell, /auction, /stats, /top, /report, /stash")); return true;
            case "stash": pl.stash().open(p); return true;
            case "stash take": pl.stash().take(p); return true;
            case "crate": if(a.length<1){ p.sendMessage(U.c("&7/crate <тип> — нужны ключи")); return true; } { String err=pl.crates().open(p,a[0]); if(err!=null) p.sendMessage(U.c("&c"+err)); } return true;
            case "quest": p.sendMessage(U.c("&7Квесты: этап 2 (заготовка).")); return true;
            case "duel": pl.duelCmd(p,a); return true;
            case "trade": pl.tradeCmd(p,a); return true;
            default: return false;
        }
    }
}