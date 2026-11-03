package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import java.util.*;
public class AuctionManager implements CommandExecutor {
    public static class Lot { public int id; public UUID seller; public String sellerName; public Item item; public double price; public long expiry; public UUID bidder; public String bidderName; }
    private final FrizProject pl;
    private final List<Lot> lots=new ArrayList<>();
    private int nextId=1;
    public AuctionManager(FrizProject pl){ this.pl=pl; }
    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] a){
        if(!(s instanceof Player)) return true;
        Player p=(Player)s;
        if(a.length==0) a=new String[]{"list"};
        switch(a[0].toLowerCase()){
            case "list": {
                p.sendMessage(U.c("&6Аукцион ("+lots.size()+"):"));
                for(Lot l: lots) p.sendMessage(U.c("&f#"+l.id+" &e"+l.item.getName()+" x"+l.item.getCount()+" &7— &a"+U.fmtMoney(l.price)+" &7от "+l.sellerName));
                return true;
            }
            case "sell": {
                if(a.length<2){ p.sendMessage(U.c("&7/auction sell <цена>")); return true; }
                double price; try{ price=Double.parseDouble(a[1]); }catch(Exception e){ p.sendMessage(U.c("&cЦена неверна")); return true; }
                Item hand=p.getInventory().getItemInHand();
                if(hand.getId()==0){ p.sendMessage(U.c("&cВозьмите предмет в руку")); return true; }
                Lot l=new Lot(); l.id=nextId++; l.seller=p.getUniqueId(); l.sellerName=p.getName();
                l.item=hand.clone(); l.price=price; l.expiry=System.currentTimeMillis()/1000+86400;
                p.getInventory().clear(p.getInventory().getHeldItemIndex());
                lots.add(l);
                p.sendMessage(U.c("&aЛот #"+l.id+" выставлен за &e"+U.fmtMoney(price)));
                return true;
            }
            case "bid": case "buy": {
                if(a.length<2){ p.sendMessage(U.c("&7/auction bid <id>")); return true; }
                Lot l=find(a[1]);
                if(l==null){ p.sendMessage(U.c("&cЛот не найден")); return true; }
                if(l.seller.equals(p.getUniqueId())){ p.sendMessage(U.c("&cНельзя купить свой лот")); return true; }
                if(!pl.eco().take(p,l.price)){ p.sendMessage(U.c("&cНе хватает денег")); return true; }
                pl.stash().give(p,l.item.clone());
                pl.eco().add(getSellerPlayer(l), l.price*0.95); // комиссия 5%
                lots.remove(l);
                p.sendMessage(U.c("&aКуплено: &f"+l.item.getName()));
                return true;
            }
            case "cancel": {
                if(a.length<2) return true;
                Lot l=find(a[1]);
                if(l==null||!l.seller.equals(p.getUniqueId())){ p.sendMessage(U.c("&cЛот не найден или не ваш")); return true; }
                pl.stash().give(p,l.item); lots.remove(l);
                p.sendMessage(U.c("&aЛот снят, предмет возвращён."));
                return true;
            }
            default: p.sendMessage(U.c("&7/auction list|sell <цена>|bid <id>|cancel <id>")); return true;
        }
    }
    private Lot find(String idS){
        try{ int id=Integer.parseInt(idS); for(Lot l: lots) if(l.id==id) return l; }catch(Exception e){}
        return null;
    }
    private Player getSellerPlayer(Lot l){
        Player t=pl.getServer().getPlayer(l.seller);
        return t!=null?t:null;
    }
    public void save(){}
}