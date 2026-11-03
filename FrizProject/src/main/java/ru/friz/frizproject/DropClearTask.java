package ru.friz.frizproject;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.scheduler.PluginTask;
import java.util.ArrayList;
public class DropClearTask extends PluginTask<FrizProject> {
    public DropClearTask(FrizProject pl){ super(pl); }
    @Override public void onRun(int t){
        pl.resetDropClear();
        int removed=0;
        for(cn.nukkit.level.Level l: pl.getServer().getLevels().values()){
            for(EntityItem ei: new ArrayList<>(l.getEntitiesByClass(EntityItem.class))){
                ei.close(); removed++;
            }
        }
        if(removed>0) pl.getServer().broadcastMessage(U.c("&6[Чистка] &fУдалено предметов на земле: &e"+removed));
    }
}