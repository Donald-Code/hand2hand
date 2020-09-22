package tshabalala.bongani.courierservice.model;

import java.io.Serializable;

public class Shipper implements Serializable {

    private String uid;
    private String name;
    private String surname;
    private String phone;
    private String from;
    private String destination;
    private String date;

    public Shipper() {
    }

    public Shipper(String uid, String name, String surname, String phone, String from, String destination, String date) {
        this.uid = uid;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.from = from;
        this.destination = destination;
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
