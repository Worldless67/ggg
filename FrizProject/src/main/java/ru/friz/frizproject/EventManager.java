package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import java.util.*;
public class EventManager {
    private final FrizProject pl;
    public final Set<String> lockedChests=new HashSet<>(); // "x,y,z" до конца таймера
    private final List<Runnable> stoppers=new ArrayList<>();
    public EventManager(FrizProject pl){ this.pl=pl; }
    public void startAll(){
        scheduleMystic(); scheduleBeaconKiller(); scheduleDeathBeacon(); scheduleFlower(); scheduleVolcano();
    }
    private Level w(){ return pl.map().level(); }
    private int rnd(int a,int b){ return a+(int)(Math.random()*(b-a+1)); }
    private Position randPos(){
        int x=pl.map().cx+rnd(-150,150), z=pl.map().cz+rnd(-150,150);
        Level l=w(); int y=l.getHighestBlockAt(x,z)+1;
        return new Position(x,y,z,l);
    }
    // 1) Мистический сундук — каждые 20 минут
    private void scheduleMystic(){
        int sec=pl.getConfig().getInt("events.mystic-interval-min",20)*60;
        pl.getServer().getScheduler().scheduleRepeatingTask(pl, ()->{
            Position p=randPos(); int tier=rnd(1,3);
            w().setBlock((int)p.x,(int)p.y,(int)p.z, Block.get(146));
            String key=(int)p.x+","+(int)p.y+","+(int)p.z;
            registerChest(p, tier, "Мистический сундук");
            String t=tier==3?"&cлегендарный":tier==2?"&6редкий":"&aобычный";
            pl.getServer().broadcastMessage(U.c("&6[Событие] &fМистический сундук (&7"+t+"&f) появился! Координаты: &e"+(int)p.x+", "+(int)p.y+", "+(int)p.z));
        }, sec*20);
    }
    // 2) Маяк-убийца — раз в час, сундук закрыт 5 минут
    private void scheduleBeaconKiller(){
        int sec=pl.getConfig().getInt("events.beacon-killer-interval-min",60)*60;
        pl.getServer().getScheduler().scheduleRepeatingTask(pl, ()->{
            Position p=randPos();
            w().setBlock((int)p.x,(int)p.y,(int)p.z, Block.get(138)); // маяк
            w().setBlock((int)p.x,(int)p.y+1,(int)p.z, Block.get(146));
            registerChest(new Position(p.x,p.y+1,p.z,w()), 3, "Маяк-убийца");
            lock(p, pl.getConfig().getInt("events.beacon-lock-sec",300));
            pl.getServer().broadcastMessage(U.c("&c[Событие] &fМаяк-убийца пробудился! Сундук откроется через &e5 мин&f. Координаты: &e"+(int)p.x+", "+(int)p.y+", "+(int)p.z));
        }, sec*20);
    }
    // 3) Маяк смерти — уровень лута объявляется
    private void scheduleDeathBeacon(){
        pl.getServer().getScheduler().scheduleRepeatingTask(pl, ()->{
            Position p=randPos(); int tier=rnd(2,3);
            w().setBlock((int)p.x,(int)p.y,(int)p.z, Block.get(138));
            w().setBlock((int)p.x,(int)p.y+1,(int)p.z, Block.get(146));
            registerChest(new Position(p.x,p.y+1,p.z,w()), tier, "Маяк смерти");
            pl.getServer().broadcastMessage(U.c("&4[Событие] &fМаяк смерти! Уровень лута: &c"+tier+"&f. Координаты: &e"+(int)p.x+", "+(int)p.y+", "+(int)p.z));
        }, 45*60*20);
    }
    // 4) Цветок — постройка с паркуром, таймер 600 сек
    private void scheduleFlower(){
        pl.getServer().getScheduler().scheduleRepeatingTask(pl, ()->{
            Position c=randPos();
            Level l=w(); int x=(int)c.x,y=(int)c.y,z=(int)c.z;
            int lockSec=pl.getConfig().getInt("events.flower-lock-sec",600);
            for(int dy=1;dy<=5;dy++) l.setBlock(x,y+dy,z,Block.get(159,2)); // стебель
            int[][] petals={{3,0},{-3,0},{0,3},{0,-3},{2,2},{-2,2},{2,-2},{-2,-2}};
            for(int[] pt:petals){ l.setBlock(x+pt[0],y+6,z+pt[1],Block.get(159,14)); l.setBlock(x+pt[0],y+6,z+pt[1],Block.get(159,14)); }
            l.setBlock(x,y+6,z,Block.get(146));
            registerChest(new Position(x,y+6,z,l), rnd(2,3), "Цветок");
            lock(new Position(x,y+6,z,l), lockSec);
            pl.getServer().broadcastMessage(U.c("&d[Событие] &fЦветок расцвёл! Паркур по стеблю к сундуку. Таймер: &e"+(lockSec/60)+" мин&f. Координаты: &e"+x+", "+(y+6)+", "+z));
        }, 35*60*20);
    }
    // 5) Мини-вулкан — лава и 4 сундука
    private void scheduleVolcano(){
        pl.getServer().getScheduler().scheduleRepeatingTask(pl, ()->{
            Position c=randPos(); Level l=w(); int x=(int)c.x,y=(int)c.y,z=(int)c.z;
            for(int dx=-3;dx<=3;dx++) for(int dz=-3;dz<=3;dz++){
                if(Math.abs(dx)==3||Math.abs(dz)==3) l.setBlock(x+dx,y+1,z+dz,Block.get(1));
                else l.setBlock(x+dx,y+1,z+dz,Block.get(11)); // лава
            }
            int[][] ch={{-3,0},{3,0},{0,-3},{0,3}};
            int tier=rnd(1,3);
            for(int[] o:ch){
                l.setBlock(x+o[0],y+2,z+o[1],Block.get(146));
                registerChest(new Position(x+o[0],y+2,z+o[1],l), tier, "Мини-вулкан");
            }
            pl.getServer().broadcastMessage(U.c("&6[Событие] &fМини-вулкан! Извержение началось, 4 сундука, уровень лута: &e"+tier+"&f. Координаты: &e"+x+", "+y+", "+z));
        }, pl.getConfig().getInt("events.volcano-interval-min",30)*60*20);
    }
    private void registerChest(Position p,int tier,String label){
        pl.registerEventChest(p, tier, label);
    }
    public void lock(Position p,int sec){
        String key=(int)p.x+","+(int)p.y+","+(int)p.z;
        lockedChests.add(key);
        pl.getServer().getScheduler().scheduleDelayedTask(pl, ()-> lockedChests.remove(key), sec*20);
    }
    public boolean isLocked(Vector3 v){ return lockedChests.contains(v.getFloorX()+","+v.getFloorY()+","+v.getFloorZ()); }
    public void stopAll(){ for(Runnable r: stoppers) r.run(); stoppers.clear(); }
}