package ru.friz.frizproject;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.utils.TextFormat;
import java.util.*;
public class NpcManager {
    private final FrizProject pl;
    private final List<FloatingTextParticle> npcs=new ArrayList<>();
    public NpcManager(FrizProject pl){ this.pl=pl; }
    public void spawnAll(){
        Position sp=pl.map().spawn(); Level l=pl.map().level();
        spawn(l, sp.add(-8,2,-6), "&6Скупщик", "Продай ресурсы: /sell");
        spawn(l, sp.add(8,2,-6), "&eАукционист", "Торги: /auction");
        spawn(l, sp.add(-8,2,6), "&aПродавец", "Магазин: /shop");
        spawn(l, sp.add(8,2,6), "&bКвестодатель", "Квесты: /quest");
        spawn(l, sp.add(0,2,10), "&dРежимы", "Скоро: этап 2");
    }
    private void spawn(Level l, Position p, String title, String sub){
        FloatingTextParticle f=new FloatingTextParticle("", TextFormat.colorize(title+"\n"+sub));
        f.setPosition(p); f.setInvisible(false);
        l.addParticle(f); npcs.add(f);
    }
    public void clear(Level l){ for(FloatingTextParticle f: npcs) f.setInvisible(true); }
}