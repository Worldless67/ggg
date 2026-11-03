package ru.friz.frizproject;
import java.util.*;
public class ReportManager {
    public static class Report { public int id; public String reporter; public String target; public String reason; public String status="открыта"; public String takenBy=""; }
    private final List<Report> reports=new ArrayList<>();
    private int nextId=1;
    public Report create(String reporter,String target,String reason){
        Report r=new Report(); r.id=nextId++; r.reporter=reporter; r.target=target; r.reason=reason;
        reports.add(r); return r;
    }
    public List<Report> open(){ List<Report> l=new ArrayList<>(); for(Report r:reports) if(r.status.equals("открыта")) l.add(r); return l; }
    public Report byId(int id){ for(Report r:reports) if(r.id==id) return r; return null; }
    public List<Report> all(){ return reports; }
}