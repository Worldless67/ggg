package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
public class StatsTask extends PluginTask<FrizProject> {
    public StatsTask(FrizProject pl){ super(pl); }
    @Override public void onRun(int t){
        for(Player p: pl.getServer().getOnlinePlayers().values()){
            pl.pdm().data(p).addPlaytime(20);
            if(pl.rm().rank(p)!=null) pl.rm().applyTag(p);
        }
        pl.pdm().save();
    }
}