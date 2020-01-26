package stepstkmce.blooddonors.Item;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable{
    String name;
    String Year;
    String branch;
    String adm;
    Date last;
    Boolean donated;
    String number;
    String place;
    String group;

    public Boolean getDonated() {
        return donated;
    }

    public void setDonated(Boolean donated) {
        this.donated = donated;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getAdm() {
        return adm;
    }

    public void setAdm(String adm) {
        this.adm = adm;
    }

    public Date getLast() {
        return last;
    }

    public void setLast(Date last) {
        this.last = last;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
