package ru.friz.frizproject;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.block.Block;
public class SpawnMap {
    private final FrizProject pl;
    public int cx,cy,cz; private boolean built=false;
    public SpawnMap(FrizProject pl){
        this.pl=pl;
        cx=pl.getConfig().getInt("spawn.x",0); cy=pl.getConfig().getInt("spawn.y",90); cz=pl.getConfig().getInt("spawn.z",0);
    }
    public Position spawn(){ return new Position(cx, cy+1, cz, level()); }
    public Level level(){ Level l=pl.getServer().getLevelByName(pl.getConfig().getString("spawn.world","FrizWorld")); return l!=null?l:pl.getServer().getDefaultLevel(); }
    public void buildIfNeeded(){
        if(built) return; built=true;
        if(pl.getConfig().getBoolean("spawnBuilt",false)){ pl.getLogger().info("Спавн уже построен — пропуск."); return; }
        build(); pl.getConfig().set("spawnBuilt",true); pl.saveConfig();
        pl.getLogger().info("Карта спавна построена.");
    }
    public void build(){
        Level l=level(); if(l==null) return;
        int R=30;
        // платформа и логотип FM из цветной шерсти
        for(int x=-R;x<=R;x++) for(int z=-R;z<=R;z++){
            l.setBlock(cx+x,cy,cz+z, Block.get(2)); // трава
            l.setBlock(cx+x,cy-1,cz+z, Block.get(3));
        }
        logoFM(l);
        fountains(l); trees(l); buildings(l); cratesArea(l); pvpRing(l);
        l.setBlock(cx,cy+1,cz,Block.get(63,10)); // табличка спавна (опционально)
    }
    private void logoFM(Level l){
        int ox=cx-6, oz=cz+20;
        byte woolL=14, woolM=3; // красный и голубой
        int[][] F={{0,0},{1,0},{2,0},{2,1},{2,2},{0,1},{0,2},{1,2}};
        int[][] M={{4,0},{5,0},{6,0},{4,1},{5,1},{6,1},{5,2}};
        for(int[] a:F) l.setBlock(ox+a[0],cy+1,oz+a[1],Block.get(35,woolL));
        for(int[] a:M) l.setBlock(ox+a[0],cy+1,oz+a[1],Block.get(35,woolM));
    }
    private void fountains(Level l){
        int[][] off={{-20,-20},{20,-20},{-20,20},{20,20}};
        for(int[] o:off){
            for(int dx=-1;dx<=1;dx++) for(int dz=-1;dz<=1;dz++) l.setBlock(cx+o[0]+dx,cy+1,cz+o[1]+dz,Block.get(9)); // вода
            l.setBlock(cx+o[0],cy+1,cz+o[1],Block.get(9));
        }
    }
    private void trees(Level l){
        int[][] off={{-25,-10},{25,-10},{-25,10},{25,10}};
        for(int[] o:off){
            int tx=cx+o[0], tz=cz+o[1];
            for(int h=1;h<=4;h++) l.setBlock(tx,cy+h,tz,Block.get(17));
            for(int dx=-2;dx<=2;dx++) for(int dz=-2;dz<=2;dz++) for(int dy=4;dy<=5;dy++)
                if(Math.abs(dx)+Math.abs(dz)+ (dy-4) <=3) l.setBlock(tx+dx,cy+dy,tz+dz,Block.get(18));
        }
    }
    private void buildings(Level l){
        building(l,cx-12,cz-26,"Магазин");
        building(l,cx+12,cz-26,"Кейсы");
    }
    private void building(Level l,int bx,int bz,String label){
        for(int x=0;x<9;x++) for(int z=0;z<7;z++){
            l.setBlock(bx+x,cy,bz+z,Block.get(1));
            for(int y=1;y<5;y++){
                boolean wall=x==0||x==8||z==0||z==6;
                l.setBlock(bx+x,cy+y,bz+z, wall?Block.get(43,0):Block.get(0));
            }
            l.setBlock(bx+x,cy+5,bz+z,Block.get(43,0));
        }
        l.setBlock(bx+4,cy+2,bz,Block.get(0));
        l.setBlock(bx+4,cy+3,bz,Block.get(0));
    }
    private void cratesArea(Level l){
        for(int i=-2;i<=2;i++) l.setBlock(cx+i*3,cy+1,cz-10,Block.get(146)); // сундуки кейсов
    }
    private void pvpRing(Level l){
        for(int a=0;a<360;a+=10){
            int x=cx+(int)Math.round(28*Math.cos(Math.toRadians(a)));
            int z=cz+(int)Math.round(28*Math.sin(Math.toRadians(a)));
            l.setBlock(x,cy+1,z,Block.get(98));
        }
    }
    public boolean inSpawn(cn.nukkit.math.Vector3 v){
        return Math.abs(v.getFloorX()-cx)<=30 && Math.abs(v.getFloorZ()-cz)<=30 && Math.abs(v.getFloorY()-cy)<=10;
    }
}