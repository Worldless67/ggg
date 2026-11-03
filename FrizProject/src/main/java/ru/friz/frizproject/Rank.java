package ru.friz.frizproject;
public enum Rank {
    PLAYER("Игрок","&7",false),
    MAGISTER("Магистр","&2",false),
    VLASTELIN("Властелин","&5",false),
    VOIN("Воин","&6",false),
    STRANNIK("Странник","&a",false),
    LORD("Лорд","&9",false),
    LEGEND("Легенда","&c",false),
    PRINC("Принц","&d",false),
    HRANITEL("Хранитель","&b",false),
    TVOREC("Творец","&6&l",false);
    public final String name; public final String color; public final boolean isStaff;
    Rank(String n,String c,boolean s){ name=n; color=c; isStaff=s; }
    public static Rank byName(String s){ try{ return Rank.valueOf(s.toUpperCase()); }catch(Exception e){ return PLAYER; } }
}