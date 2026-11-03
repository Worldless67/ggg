package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Effect;
import cn.nukkit.math.Vector3;
import java.util.*;
public class Listeners implements Listener {
    private final FrizProject pl;
    private final Map<String,Integer> poisonHits=new HashMap<>();
    public Listeners(FrizProject pl){ this.pl=pl; }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p=e.getPlayer();
        PlayerData d=pl.pdm().data(p);
        if(d.name().isEmpty()) d.setName(p.getName());
        pl.rm().applyTag(p);
        p.sendMessage(U.c("&6FrizMine &f— добро пожаловать! &7/kits, /shop, /donateshop"));
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Player p=e.getPlayer();
        if(pl.map().inSpawn(e.getBlock()) && pl.rm().rank(p)!=Rank.TVOREC) e.setCancelled(true);
        else pl.pdm().data(p).addBreak(1);
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        Player p=e.getPlayer();
        if(pl.map().inSpawn(e.getBlock()) && pl.rm().rank(p)!=Rank.TVOREC) e.setCancelled(true);
    }
    @EventHandler
    public void onChest(PlayerInteractEvent e){
        if(e.getBlock().getId()!=146) return;
        Block b=e.getBlock();
        String key=b.getFloorX()+","+b.getFloorY()+","+b.getFloorZ();
        Integer tier=pl.eventChest(key);
        if(tier==null) return;
        if(pl.events().isLocked(new Vector3(b.getFloorX(),b.getFloorY(),b.getFloorZ()))){
            e.setCancelled(true);
            e.getPlayer().sendMessage(U.c("&cСундук ещё закрыт! Ждите открытия."));
            return;
        }
        e.getPlayer().sendMessage(U.c("&aВы открыли сундук события! Уровень лута: &e"+tier));
        pl.claimEventChest(key, e.getPlayer());
    }
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player)) return;
        Player attacker=(Player)e.getDamager();
        Item hand=attacker.getInventory().getItemInHand();
        String fid=U.frizId(hand);
        if(fid.equals("w_vampire")){
            double heal=Math.max(1,e.getFinalDamage()*0.2);
            double h=attacker.getHealth()+heal;
            attacker.setHealth(Math.min(attacker.getMaxHealth(),h));
        }
        if(fid.equals("w_poison")){
            String k=attacker.getName()+"|"+e.getEntity().getId();
            int n=poisonHits.getOrDefault(k,0)+1;
            poisonHits.put(k,n);
            if(n%2==0){
                Entity victim=e.getEntity();
                try{ victim.addEffect(Effect.getEffect(19).setDuration(60).setAmplifier(1)); }catch(Throwable ignored){}
            }
        }
    }
    @EventHandler
    public void onDeath(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p=(Player)e.getEntity();
            if(e.getFinalDamage()>=p.getHealth()){
                pl.pdm().data(p).setDeaths(pl.pdm().data(p).deaths()+1);
                if(e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)e).getDamager() instanceof Player){
                    Player k=(Player)((EntityDamageByEntityEvent)e).getDamager();
                    pl.pdm().data(k).setKills(pl.pdm().data(k).kills()+1);
                }
            }
        }
    }
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e){
        // магнит: предметы тянутся к игрокам с талисманом/магнитом рядом
        try{
            Entity it=e.getEntity();
            for(Player p: it.getViewers().values()){
                if(pl.hasMagnet(p) && p.distance(it)<5){
                    it.setMotion(p.subtract(it).normalize().multiply(0.3));
                    break;
                }
            }
        }catch(Throwable ignored){}
    }
}