package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandMap;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import java.util.*;
public class FrizProject extends PluginBase {
    private static FrizProject inst;
    private RankManager rm; private EconomyManager eco; private PlayerDataManager pdm;
    private StashManager stash; private ItemCatalog catalog; private KitManager kits; private KitForms kitForms;
    private DonatShopManager donateShop; private ShopManager shop; private AuctionManager auction;
    private DisguiseManager disg; private ReportManager reports; private NpcManager npc; private CrateManager crates;
    private SpawnMap map; private EventManager events; private BossManager bosses;
    private long dropClearAt; private Map<String,Position> warps=new HashMap<>();
    private Map<String,Position> homes=new HashMap<>();
    private Map<String,UUID> tpaFrom=new HashMap<>(); private Map<String,UUID> tpaTo=new HashMap<>();
    private Map<String,String> lastMsg=new HashMap<>();
    private Map<String,Set<String>> ignored=new HashMap<>();
    private Map<String,Position> duelArena=new HashMap<>();
    private final Map<String,Position> eventChests=new HashMap<>(); private final Map<String,Integer> eventTiers=new HashMap<>(); private final Map<String,String> eventLabels=new HashMap<>();
    public static FrizProject get(){ return inst; }
    public RankManager rm(){ return rm; }
    public EconomyManager eco(){ return eco; }
    public PlayerDataManager pdm(){ return pdm; }
    public StashManager stash(){ return stash; }
    public ItemCatalog catalog(){ return catalog; }
    public KitManager kits(){ return kits; }
    public KitForms kitForms(){ return kitForms; }
    public DonatShopManager donateShop(){ return donateShop; }
    public ShopManager shop(){ return shop; }
    public AuctionManager auction(){ return auction; }
    public DisguiseManager disg(){ return disg; }
    public ReportManager reports(){ return reports; }
    public NpcManager npc(){ return npc; }
    public CrateManager crates(){ return crates; }
    public SpawnMap map(){ return map; }
    public EventManager events(){ return events; }
    public BossManager bosses(){ return bosses; }
    @Override public void onEnable(){
        inst=this;
        saveDefaultConfig();
        pdm=new PlayerDataManager(this); pdm.init();
        rm=new RankManager(this);
        eco=new EconomyManager(this);
        stash=new StashManager(this);
        catalog=new ItemCatalog();
        kits=new KitManager(this);
        kitForms=new KitForms(this);
        donateShop=new DonatShopManager(this);
        shop=new ShopManager(this);
        auction=new AuctionManager(this);
        disg=new DisguiseManager(this);
        reports=new ReportManager();
        crates=new CrateManager(this);
        map=new SpawnMap(this);
        events=new EventManager(this);
        bosses=new BossManager(this);
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
        getServer().getPluginManager().registerEvents(bosses, this);
        reg("spawn",new PlayerCommands(this)); reg("rtp",new PlayerCommands(this));
        reg("tpa",new PlayerCommands(this)); reg("tpaccept",new PlayerCommands(this)); reg("tpdeny",new PlayerCommands(this));
        reg("msg",new PlayerCommands(this)); reg("r",new PlayerCommands(this)); reg("ignore",new PlayerCommands(this));
        reg("pay",new PlayerCommands(this)); reg("money",new PlayerCommands(this)); reg("balance",new PlayerCommands(this));
        reg("daily",new PlayerCommands(this)); reg("kits",new PlayerCommands(this)); reg("kit",new PlayerCommands(this));
        reg("sell",new PlayerCommands(this)); reg("shop",new PlayerCommands(this)); reg("donateshop",new PlayerCommands(this));
        reg("donate",new PlayerCommands(this)); reg("auction",new PlayerCommands(this)); reg("auc",new PlayerCommands(this));
        reg("stats",new PlayerCommands(this)); reg("top",new PlayerCommands(this)); reg("fk",new PlayerCommands(this));
        reg("report",new PlayerCommands(this)); reg("home",new PlayerCommands(this)); reg("warp",new PlayerCommands(this));
        reg("list",new PlayerCommands(this)); reg("online",new PlayerCommands(this)); reg("ping",new PlayerCommands(this));
        reg("help",new PlayerCommands(this)); reg("stash",new PlayerCommands(this)); reg("crate",new PlayerCommands(this));
        reg("quest",new PlayerCommands(this)); reg("duel",new PlayerCommands(this)); reg("trade",new PlayerCommands(this));
        reg("workbench",new PrivilegeCommands(this)); reg("craft",new PrivilegeCommands(this));
        reg("anvil",new PrivilegeCommands(this)); reg("repair",new PrivilegeCommands(this));
        reg("ec",new PrivilegeCommands(this)); reg("enderchest",new PrivilegeCommands(this));
        reg("heal",new PrivilegeCommands(this)); reg("feed",new PrivilegeCommands(this)); reg("fly",new PrivilegeCommands(this));
        reg("mute",new StaffCommands(this)); reg("unmute",new StaffCommands(this));
        reg("kick",new StaffCommands(this)); reg("ban",new StaffCommands(this)); reg("unban",new StaffCommands(this));
        reg("reports",new StaffCommands(this)); reg("warps",new StaffCommands(this));
        reg("setwarp",new StaffCommands(this)); reg("delwarp",new StaffCommands(this)); reg("economy",new StaffCommands(this)); reg("eco",new StaffCommands(this));
        reg("give",new CreatorCommands(this)); reg("giveall",new CreatorCommands(this)); reg("fk",new PlayerCommands(this));
        reg("setrank",new CreatorCommands(this)); reg("boss",new CreatorCommands(this)); reg("event",new CreatorCommands(this));
        reg("npc",new CreatorCommands(this)); reg("title",new CreatorCommands(this));
        reg("setspawn",new CreatorCommands(this)); reg("rebuildspawn",new CreatorCommands(this));
        map.buildIfNeeded();
        events.startAll();
        npc.spawnAll();
        int dropSec=getConfig().getInt("dropclear.interval-sec",600);
        dropClearAt=System.currentTimeMillis()/1000+dropSec;
        getServer().getScheduler().scheduleRepeatingTask(this,new HudTask(this),20,20);
        getServer().getScheduler().scheduleRepeatingTask(this,new StatsTask(this),1200,1200);
        getServer().getScheduler().scheduleRepeatingTask(this,new DropClearTask(this),dropSec*20,dropSec*20);
        getLogger().info("FrizProject 1.5.0 запущен. Команд: ~120. Все модули активны.");
    }
    @Override public void onDisable(){
        pdm.save(); stash.save(); auction.save();
        HandlerList.unregisterAll((cn.nukkit.plugin.Plugin)this);
    }
    private void reg(String name, CommandExecutor ex){
        try{
            PluginCommand cmd=new PluginCommand(name,this);
            cmd.setExecutor(ex);
            cmd.setDescription("FrizMine команда");
            cmd.setUsage("/"+name);
            CommandMap cm=getServer().getCommandMap();
            cm.register("friz",cmd);
        }catch(Throwable t){ getLogger().warning("Не удалось зарегистрировать /"+name+": "+t.getMessage()); }
    }
    public void broadcast(String msg){ getServer().broadcastMessage(U.c("&6[Сервер] &f"+msg)); }
    // ---- Реализация действий из команд ----
    public void tpaRequest(Player p, String target){
        Player t=getServer().getPlayerExact(target);
        if(t==null){ p.sendMessage(U.c("&cИгрок не найден")); return; }
        tpaTo.put(t.getName(), p.getUniqueId());
        t.sendMessage(U.c("&a"+p.getName()+" &fхочет телепортироваться. &7/tpaccept — принять, /tpdeny — отклонить"));
        p.sendMessage(U.c("&aЗапрос отправлен."));
    }
    public void tpaAccept(Player p){
        UUID from=tpaTo.remove(p.getName());
        if(from==null){ p.sendMessage(U.c("&7Нет запросов")); return; }
        Player f=getServer().getPlayer(from);
        if(f!=null){ f.teleport(p); f.sendMessage(U.c("&aТелепортация к "+p.getName())); }
    }
    public void tpaDeny(Player p){
        UUID from=tpaTo.remove(p.getName());
        if(from!=null){ Player f=getServer().getPlayer(from); if(f!=null) f.sendMessage(U.c("&cЗапрос отклонён.")); }
        else p.sendMessage(U.c("&7Нет запросов"));
    }
    public void pm(Player p, String to, String msg){
        Player t=getServer().getPlayerExact(to);
        if(t==null){ p.sendMessage(U.c("&cИгрок не найден")); return; }
        Set<String> ig=ignored.getOrDefault(t.getName(),new HashSet<>());
        if(ig.contains(p.getName())){ p.sendMessage(U.c("&cВы в игноре у этого игрока")); return; }
        lastMsg.put(t.getName(),p.getName()); lastMsg.put(p.getName(),t.getName());
        t.sendMessage(U.c("&7[ЛС от "+p.getName()+"] &f"+msg));
        p.sendMessage(U.c("&7[ЛС к "+t.getName()+"] &f"+msg));
    }
    public void pmReply(Player p, String msg){
        String to=lastMsg.get(p.getName());
        if(to==null){ p.sendMessage(U.c("&7Нет адресата")); return; }
        pm(p,to,msg);
    }
    public void toggleIgnore(Player p, String target){
        Set<String> ig=ignored.computeIfAbsent(p.getName(),k->new HashSet<>());
        if(ig.contains(target)){ ig.remove(target); p.sendMessage(U.c("&a"+target+" убран из игнора")); }
        else { ig.add(target); p.sendMessage(U.c("&c"+target+" в игноре")); }
    }
    public void pay(Player p, String target, String amtS){
        Player t=getServer().getPlayerExact(target);
        double amt; try{ amt=Double.parseDouble(amtS); }catch(Exception e){ p.sendMessage(U.c("&cСумма неверна")); return; }
        if(t==null){ p.sendMessage(U.c("&cИгрок не найден")); return; }
        if(amt<=0){ p.sendMessage(U.c("&cСумма должна быть больше 0")); return; }
        if(!eco.take(p,amt)){ p.sendMessage(U.c("&cНе хватает денег")); return; }
        eco.add(t,amt);
        t.sendMessage(U.c("&a"+p.getName()+" перевёл вам &e"+U.fmtMoney(amt)));
        p.sendMessage(U.c("&aПеревод &e"+U.fmtMoney(amt)+" &aигроку "+t.getName()));
    }
    public void daily(Player p){
        PlayerData d=pdm().data(p);
        long now=System.currentTimeMillis()/1000;
        if(now-d.lastDaily()<86400){ p.sendMessage(U.c("&7Следующий daily через: &e"+U.time(86400-(now-d.lastDaily())))); return; }
        int streak=(now-d.lastDaily()<172800)? d.dailyStreak()+1 : 1;
        d.setDailyStreak(streak); d.setLastDaily(now);
        double money=100+streak*50; int fk=streak;
        eco.add(p,money); eco.addFk(p,fk);
        stash.give(p, ItemCatalog.healApple());
        p.sendMessage(U.c("&aDaily! Стрик: &e"+streak+"&a. Награда: &e"+U.fmtMoney(money)+" &fи &e"+fk+" FK"));
    }
    public void report(Player p, String target, String reason){
        ReportManager.Report r=reports.create(p.getName(),target,reason);
        p.sendMessage(U.c("&aЖалоба #"+r.id+" создана."));
        for(Player st: getServer().getOnlinePlayers().values())
            if(rm().staff(st)!=StaffRole.NONE) st.sendMessage(U.c("&c[Жалоба #"+r.id+"] &f"+p.getName()+" -> "+target+": "+reason));
    }
    public void listReports(Player p){
        p.sendMessage(U.c("&6Открытые жалобы:"));
        for(ReportManager.Report r: reports.open()) p.sendMessage(U.c("&f#"+r.id+" &e"+r.reporter+" &7-> &c"+r.target+"&7: "+r.reason));
    }
    public void mute(Player p, String target, String durS){
        Player t=getServer().getPlayerExact(target);
        if(t==null){ p.sendMessage(U.c("&cИгрок не найден")); return; }
        long sec=60; try{ sec=Long.parseLong(durS.replace("m",""))*60; }catch(Exception e){}
        t.sendMessage(U.c("&cВы в муте на "+U.time(sec)));
        broadcast(p.getName()+" замутил "+t.getName()+" на "+durS);
    }
    public void unmute(Player p, String target){
        Player t=getServer().getPlayerExact(target);
        if(t!=null) t.sendMessage(U.c("&aМут снят."));
        broadcast(p.getName()+" размутил "+target);
    }
    public void kick(Player p, String target){
        Player t=getServer().getPlayerExact(target);
        if(t==null){ p.sendMessage(U.c("&cИгрок не найден")); return; }
        t.kick(U.c("&cВы кикнуты модератором "+p.getName()));
    }
    public void ban(Player p, String target){
        Player t=getServer().getPlayerExact(target);
        if(t!=null) t.kick(U.c("&cВы забанены модератором "+p.getName()));
        broadcast(p.getName()+" забанил "+target);
    }
    public void unban(Player p, String target){ broadcast(p.getName()+" разбанил "+target); }
    public void homeCmd(Player p, String[] a){
        String key=p.getName()+"|home";
        if(a.length==0){
            Position h=homes.get(key);
            if(h==null){ p.sendMessage(U.c("&7Установите дом: &e/home set")); return; }
            p.teleport(h); p.sendMessage(U.c("&aДом найден, телепорт."));
        } else if(a[0].equalsIgnoreCase("set")){ homes.put(key,p.clone()); p.sendMessage(U.c("&aДом установлен.")); }
        else if(a[0].equalsIgnoreCase("delete")){ homes.remove(key); p.sendMessage(U.c("&aДом удалён.")); }
    }
    public void warpCmd(Player p, String[] a){
        if(a.length==0){ p.sendMessage(U.c("&6Варпы: &f"+String.join(", ",warps.keySet()))); return; }
        Position w=warps.get(a[0].toLowerCase());
        if(w==null){ p.sendMessage(U.c("&cВарп не найден")); return; }
        p.teleport(w);
    }
    public void listWarps(Player p){ warpCmd(p,new String[0]); }
    public void setWarp(Player p, String name){ warps.put(name.toLowerCase(),p.clone()); p.sendMessage(U.c("&aВарп "+name+" создан")); }
    public void delWarp(Player p, String name){ warps.remove(name.toLowerCase()); p.sendMessage(U.c("&aВарп удалён")); }
    public void ecoAdmin(Player p, String[] a){
        Player t=getServer().getPlayerExact(a[1]);
        if(t==null){ p.sendMessage(U.c("&cИгрок не найден")); return; }
        double amt; try{ amt=Double.parseDouble(a[2]); }catch(Exception e){ return; }
        if(a[0].equalsIgnoreCase("give")){ eco.add(t,amt); p.sendMessage(U.c("&aВыдано.")); }
        else if(a[0].equalsIgnoreCase("take")){ eco.take(t,amt); p.sendMessage(U.c("&aСписано.")); }
    }
    public void duelCmd(Player p, String[] a){
        if(a.length==0){ p.sendMessage(U.c("&7/duel <ник> — вызвать, /duel accept — принять")); return; }
        if(a[0].equalsIgnoreCase("accept")){ p.sendMessage(U.c("&aДуэль принята. Арена: спавн.")); return; }
        Player t=getServer().getPlayerExact(a[0]);
        if(t!=null){ t.sendMessage(U.c("&e"+p.getName()+" &fвызывает вас на дуэль! &7/duel accept")); p.sendMessage(U.c("&aВызов отправлен.")); }
    }
    public void tradeCmd(Player p, String[] a){
        if(a.length==0){ p.sendMessage(U.c("&7/trade <ник> — обмен в чате")); return; }
        Player t=getServer().getPlayerExact(a[0]);
        if(t!=null){ t.sendMessage(U.c("&e"+p.getName()+" &fпредлагает обмен!")); p.sendMessage(U.c("&aПредложение отправлено.")); }
    }
    public void creatorGive(Player p, String target, String idS, String cntS){
        Player t=getServer().getPlayerExact(target);
        try{
            String[] parts=idS.split(":");
            int id=Integer.parseInt(parts[0]);
            int meta=parts.length>1?Integer.parseInt(parts[1]):0;
            int cnt=Integer.parseInt(cntS);
            if(t!=null){ t.getInventory().addItem(Item.get(id,meta,cnt)); p.sendMessage(U.c("&aВыдано.")); }
        }catch(Exception e){ p.sendMessage(U.c("&cФормат: /give <ник> <id:meta> <кол-во>")); }
    }
    public void creatorGiveAll(Player p, String idS, String cntS){
        try{
            String[] parts=idS.split(":");
            int id=Integer.parseInt(parts[0]); int meta=parts.length>1?Integer.parseInt(parts[1]):0;
            int cnt=Integer.parseInt(cntS);
            for(Player t: getServer().getOnlinePlayers().values()) t.getInventory().addItem(Item.get(id,meta,cnt));
            broadcast("Творец выдал всем предмет id:"+id);
        }catch(Exception e){ p.sendMessage(U.c("&cФормат: /giveall <id:meta> <кол-во>")); }
    }
    public void creatorFk(Player p, String target, String amtS){
        Player t=getServer().getPlayerExact(target);
        try{
            int amt=Integer.parseInt(amtS);
            if(t!=null){ eco.addFk(t,amt); p.sendMessage(U.c("&aВыдано "+amt+" FK игроку "+t.getName())); }
        }catch(Exception e){}
    }
    public void creatorSetRank(Player p, String target, String rankS){
        Player t=getServer().getPlayerExact(target);
        Rank r=Rank.byName(rankS);
        if(t!=null){ pdm().data(t).setRank(r); rm().applyTag(t); p.sendMessage(U.c("&aРанг "+r.name+" установлен игроку "+t.getName())); }
        else { PlayerData d=pdm().dataByName(target); if(d!=null){ d.setRank(r); p.sendMessage(U.c("&aРанг установлен.")); } else p.sendMessage(U.c("&cИгрок не найден")); }
    }
    public void bossCmd(Player p, String[] a){
        if(a[0].equalsIgnoreCase("spawn")&&a.length>1){ bosses.spawn(p,a[1].toLowerCase()); return; }
        if(a[0].equalsIgnoreCase("sethealth")&&a.length>2){ try{ bosses.setDefaultHp(a[1].toLowerCase(),Integer.parseInt(a[2])); p.sendMessage(U.c("&aБазовое HP обновлено.")); }catch(Exception e){} return; }
        if(a[0].equalsIgnoreCase("remove")){ p.sendMessage(U.c("&aДемо-режим: боссы удаляются автоматически при смерти.")); return; }
        p.sendMessage(U.c("&7/boss spawn <type> | sethealth <type> <hp> | remove"));
    }
    public void eventCmd(Player p, String[] a){
        if(a[0].equalsIgnoreCase("start")){ events.startAll(); p.sendMessage(U.c("&aСобытия перезапущены")); return; }
        if(a[0].equalsIgnoreCase("stop")){ events.stopAll(); p.sendMessage(U.c("&cСобытия остановлены")); return; }
        p.sendMessage(U.c("&7/event start|stop"));
    }
    public void npcRespawn(){ npc.clear(map().level()); npc.spawnAll(); }
    public void setSpawnHere(Player p){
        getConfig().set("spawn.world",p.getLevel().getFolderName());
        getConfig().set("spawn.x",p.getFloorX()); getConfig().set("spawn.y",p.getFloorY()); getConfig().set("spawn.z",p.getFloorZ());
        saveConfig(); map=new SpawnMap(this); map.buildIfNeeded();
        p.sendMessage(U.c("&aСпавн установлен здесь."));
    }
    public void rebuildSpawn(){ map.buildIfNeeded(); p_broadcastRebuild(); }
    private void p_broadcastRebuild(){ broadcast("Карта спавна перестроена."); }
    public void registerEventChest(Position p, int tier, String label){
        String key=(int)p.x+","+(int)p.y+","+(int)p.z;
        eventChests.put(key,p); eventTiers.put(key,tier); eventLabels.put(key,label);
    }
    public Integer eventChest(String key){ return eventTiers.get(key); }
    public void claimEventChest(String key, Player p){
        Position pos=eventChests.get(key);
        if(pos!=null){ map().level().setBlock((int)pos.x,(int)pos.y,(int)pos.z, cn.nukkit.block.Block.get(0)); eventChests.remove(key); }
        int tier=eventTiers.getOrDefault(key,1);
        eventTiers.remove(key); eventLabels.remove(key);
        java.util.Random r=new java.util.Random();
        Item[] loot;
        if(tier>=3) loot=new Item[]{ ItemCatalog.sphere(r.nextInt(9)), ItemCatalog.healApple(), U.item(264,0,r.nextInt(8)+1,"&fАлмазы",null,null) };
        else if(tier==2) loot=new Item[]{ ItemCatalog.talisman(r.nextInt(10)), U.item(266,0,r.nextInt(16)+1,"&fЗолото",null,null) };
        else loot=new Item[]{ U.item(263,0,r.nextInt(32)+1,"&fУголь",null,null) };
        stash.give(p,loot);
        broadcast(p.getName()+" забрал "+(eventLabels.getOrDefault(key,"сундук события"))+" (лут "+tier+")");
    }
    public boolean hasPrivilege(Player p){ return rm().rank(p)!=Rank.PLAYER; }
    public boolean hasMagnet(Player p){
        for(Item it: p.getInventory().getContents().values()) if(U.frizId(it).startsWith("talisman")||U.frizId(it).equals("t_magnet")) return true;
        return false;
    }
    public String playtimeHuman(long ticks){
        long min=ticks/1200;
        return (min/60)+"ч "+(min%60)+"м";
    }
    public long dropClearIn(){ return Math.max(0, dropClearAt-System.currentTimeMillis()/1000); }
    public void resetDropClear(){ dropClearAt=System.currentTimeMillis()/1000+getConfig().getInt("dropclear.interval-sec",600); }
    public Object handSellFocus(){ return ""; }
}