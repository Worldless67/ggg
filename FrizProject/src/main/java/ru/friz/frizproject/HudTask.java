package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.TextFormat;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
public class HudTask extends PluginTask<FrizProject> {
    private static final DateTimeFormatter DF=DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
    private static int serverNum=1;
    public HudTask(FrizProject pl){ super(pl); }
    @Override public void onRun(int t){
        long untilClear=pl.dropClearIn();
        for(Player p: pl.getServer().getOnlinePlayers().values()){
            PlayerData d=pl.pdm().data(p);
            String rank=d.rank().color+d.rank().name;
            List<String> lines=new ArrayList<>();
            lines.add("&d&lFriz&f&lMine");
            lines.add("&fНик: &b"+(d.disguiseNick().isEmpty()?p.getName():d.disguiseNick()));
            lines.add("&fРанг: "+rank);
            lines.add("&fБаланс: &a"+U.fmtMoney(d.money()));
            lines.add("&fНаиграно: &6"+pl.playtimeHuman(d.playtime()));
            lines.add("&fY: &c"+p.getFloorY()+"  &fC: &c"+(pl.eco().fk(p)));
            lines.add("&fДо очистки: &b"+U.time(untilClear));
            lines.add("&fОнлайн: &e"+pl.getServer().getOnlinePlayers().size());
            lines.add("&f"+DF.format(LocalDateTime.now())+" &7#"+serverNum);
            show(p, lines);
        }
    }
    private void show(Player p, List<String> lines){
        try{
            Class<?> sbC=Class.forName("cn.nukkit.scoreboard.scoreboard.Scoreboard");
            Class<?> fsC=Class.forName("cn.nukkit.scoreboard.scorer.FakeScorer");
            Object sb=null;
            for(Constructor<?> c: sbC.getConstructors()){
                Class<?>[] pt=c.getParameterTypes();
                if(pt.length==2 && pt[0]==String.class && pt[1]==String.class){ sb=c.newInstance("friz", TextFormat.colorize("&dFriz&fMine")); break; }
            }
            if(sb==null){
                Class<?> slot=Class.forName("cn.nukkit.scoreboard.scoreboard.DisplaySlot");
                for(Constructor<?> c: sbC.getConstructors()){
                    Class<?>[] pt=c.getParameterTypes();
                    if(pt.length==3 && pt[0]==String.class){ sb=c.newInstance("friz", TextFormat.colorize("&dFriz&fMine"), null); break; }
                    if(pt.length==3 && pt[0]==String.class && pt[2]==String.class){ sb=c.newInstance("friz", TextFormat.colorize("&dFriz&fMine"), "sid"); break; }
                }
            }
            if(sb==null) return;
            Method setLine=null;
            for(String n: new String[]{"setLine","setScore","updateLine"}){
                try{ setLine=sbC.getMethod(n,int.class,String.class); break; }catch(Exception ignored){}
            }
            if(setLine!=null){ int i=0; for(String l: lines) setLine.invoke(sb,i++,TextFormat.colorize(l)); }
            try{ sbC.getMethod("setDisplayName",String.class).invoke(sb, TextFormat.colorize("&dFriz&fMine")); }catch(Exception ignored){}
            Method addViewer=null;
            try{ addViewer=sbC.getMethod("addViewer",Player.class); }catch(Exception ignored){}
            if(addViewer==null){
                Class<?> slot=Class.forName("cn.nukkit.scoreboard.scoreboard.DisplaySlot");
                try{ addViewer=sbC.getMethod("addViewer",Player.class,slot); }catch(Exception ignored){}
            }
            if(addViewer!=null){
                Class<?> slot=Class.forName("cn.nukkit.scoreboard.scoreboard.DisplaySlot");
                Object side=slot.getField("SIDEBAR").get(null);
                if(addViewer.getParameterCount()==2) addViewer.invoke(sb,p,side); else addViewer.invoke(sb,p);
            }
            for(String n: new String[]{"update","apply","send"}){
                try{ sbC.getMethod(n).invoke(sb); break; }catch(Exception ignored){}
            }
        }catch(Throwable ignored){}
    }
}