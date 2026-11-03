package ru.friz.frizproject;
import cn.nukkit.Player;
import cn.nukkit.form.element.SimpleResponse;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindowSimple;
public class KitForms {
    private final FrizProject pl;
    public KitForms(FrizProject pl){ this.pl=pl; }
    public void main(Player p){
        FormWindowSimple f=new FormWindowSimple(U.c("&2Киты"));
        f.addButton(new SimpleResponse(U.c("&2Обычные наборы\n&7Наборов: "+KitManager.NORMAL_KITS.length)));
        f.addButton(new SimpleResponse(U.c("&cМедия наборы\n&7Наборов: "+KitManager.MEDIA_KITS.length)));
        f.addHandler(new FormResponseHandler() {
            public void handle(Player pl2, FormWindowSimple fw) {
                if(fw.getResponse()==null) return;
                int id=fw.getResponse().getClickedButtonId();
                if(id==0) list(p, KitManager.NORMAL_KITS, "Обычные наборы");
                else list(p, KitManager.MEDIA_KITS, "Медия наборы");
            }
        });
        p.showFormWindow(f);
    }
    private void list(Player p, String[] kits, String title){
        FormWindowSimple f=new FormWindowSimple(U.c("&2"+title));
        for(String k: kits) f.addButton(new SimpleResponse(U.c(pl.kits().canTake(p,k)?"&2":"&c")+k+"\n&7Нажмите, чтобы посмотреть"));
        f.addHandler(new FormResponseHandler() {
            public void handle(Player pl2, FormWindowSimple fw) {
                if(fw.getResponse()==null) return;
                int id=fw.getResponse().getClickedButtonId();
                if(id>=0 && id<kits.length) card(p, kits[id]);
            }
        });
        p.showFormWindow(f);
    }
    private void card(Player p, String kit){
        long rem=pl.kits().remaining(p,kit);
        FormWindowSimple f=new FormWindowSimple(U.c("&c"+kit));
        String content="&c"+kit+"\n";
        content+= rem>0 ? "&fПолучить снова можно будет через: &a"+U.time(rem) : "&fДоступно сейчас";
        content+="\n&fДоступно от привилегии: &7"+pl.kits().requiredRank(kit);
        f.setContent(content);
        f.addButton(new SimpleResponse(U.c("&fПолучить кит")));
        f.addButton(new SimpleResponse(U.c("&fПосмотреть содержимое")));
        f.addButton(new SimpleResponse(U.c("&fНазад")));
        f.addHandler(new FormResponseHandler() {
            public void handle(Player pl2, FormWindowSimple fw) {
                if(fw.getResponse()==null) return;
                switch(fw.getResponse().getClickedButtonId()){
                    case 0: { String err=pl.kits().give(p,kit); p.sendMessage(U.c(err!=null?"&c"+err:"&aКит получен! Проверьте /stash")); break; }
                    case 1: { StringBuilder sb=new StringBuilder("&6Содержимое "+kit+":\n"); for(cn.nukkit.item.Item it: pl.kits().contents(kit)) sb.append("&f- ").append(it.getName()).append(" x").append(it.getCount()).append("\n"); p.sendMessage(U.c(sb.toString())); break; }
                    case 2: main(p); break;
                }
            }
        });
        p.showFormWindow(f);
    }
}