package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
public class ShopManager {
    private final FrizProject pl;
    public ShopManager(FrizProject pl){ this.pl=pl; }
    public double sellPrice(Item it){
        double v=pl.getConfig().getDouble("shop.sell."+it.getId(), -1);
        if(v>0) return v;
        String k=it.getName().toLowerCase();
        if(k.contains("diamond")) return 100;
        if(k.contains("gold")) return 50;
        if(k.contains("iron")) return 30;
        if(k.contains("coal")) return 10;
        return -1;
    }
    public void sellHand(Player p){
        Item it=p.getInventory().getItemInHand();
        if(it.getId()==0){ p.sendMessage(U.c("&7Возьмите предмет в руку.")); return; }
        double pr=sellPrice(it);
        if(pr<=0){ p.sendMessage(U.c("&7Этот предмет не продаётся.")); return; }
        double total=pr*it.getCount();
        p.getInventory().removeItem(it);
        pl.eco().add(p,total);
        p.sendMessage(U.c("&aПродано за &e"+U.fmtMoney(total)+"&a монет."));
    }
    public void sellAll(Player p){
        double total=0; int cnt=0;
        for(Item it: new ArrayList<>(p.getInventory().getContents().values())){
            if(it==null||it.getId()==0) continue;
            double pr=sellPrice(it);
            if(pr>0){ total+=pr*it.getCount(); cnt++; p.getInventory().remove(it); }
        }
        if(total<=0){ p.sendMessage(U.c("&7Продавать нечего.")); return; }
        pl.eco().add(p,total);
        p.sendMessage(U.c("&aПродано "+cnt+" видов на &e"+U.fmtMoney(total)+"&a монет."));
    }
}