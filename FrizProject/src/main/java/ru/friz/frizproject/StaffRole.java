package ru.friz.frizproject;
public enum StaffRole {
    NONE("Нет","&7",0),
    DETEID("Детейд","&8",1),
    HELPER("Хелпер","&a",2),
    MODER("Модератор","&2",3),
    SENIOR_MODER("Старший модератор","&b",4),
    SENIOR_MODER2("Старшая модерация","&3",5),
    CURATOR("Куратор","&9",6),
    HEAD_STAFF("Глава персонала","&5",7),
    ADMIN("Админ","&c",8),
    TVOREC("Творец","&6&l",9);
    public final String name; public final String color; public final int level;
    StaffRole(String n,String c,int l){ name=n; color=c; level=l; }
    public static StaffRole byName(String s){ try{ return StaffRole.valueOf(s.toUpperCase()); }catch(Exception e){ return NONE; } }
}