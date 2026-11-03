package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
public class PrivilegeCommands implements CommandExecutor {
    private final FrizProject pl;
    public PrivilegeCommands(FrizProject pl){ this.pl=pl; }
    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] a){
        if(!(s instanceof Player)){ s.sendMessage("Только для игроков"); return true; }
        Player p=(Player)s; Rank r=pl.rm().rank(p);
        switch(c.getName().toLowerCase()){
            case "workbench": case "craft":
                if(!pl.hasPrivilege(p)) { p.sendMessage(U.c("&cТолько для привилегий")); return true; }
                p.addWindow(p.getCraftingWindow()); return true;
            case "anvil": case "repair":
                if(!pl.hasPrivilege(p)) { p.sendMessage(U.c("&cТолько для привилегий")); return true; }
                p.addWindow(p.getAnvilWindow()); return true;
            case "ec": case "enderchest":
                if(!pl.hasPrivilege(p)) { p.sendMessage(U.c("&cТолько для привилегий")); return true; }
                p.addWindow(p.getEnderChestInventory()); return true;
            case "heal":
                if(!pl.hasPrivilege(p)) { p.sendMessage(U.c("&cТолько для привилегий")); return true; }
                p.setHealth(p.getMaxHealth()); p.sendMessage(U.c("&aВы вылечены.")); return true;
            case "feed":
                if(!pl.hasPrivilege(p)) { p.sendMessage(U.c("&cТолько для привилегий")); return true; }
                p.getFoodData().setLevel(20); p.sendMessage(U.c("&aВы сыты.")); return true;
            case "fly":
                if(!pl.hasPrivilege(p)) { p.sendMessage(U.c("&cТолько для привилегий")); return true; }
                p.setAllowFlight(!p.getAllowFlight()); p.sendMessage(U.c("&aРежим полёта: "+(p.getAllowFlight()?"вкл":"выкл"))); return true;
            default: return false;
        }
    }
}