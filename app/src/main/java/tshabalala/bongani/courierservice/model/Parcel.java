package tshabalala.bongani.courierservice.model;

import java.io.Serializable;

public class Parcel implements Serializable {

    private String uid;
    private String notify_uid;
    private String name;
    private String surname;
    private String phone;
    private String description;
    private String pickup;
    private String destination;
    private String category;
    private double weight;
    private String timestamp;
    private double price;
    private String status;

    public Parcel() {
    }

    public Parcel(String uid, String notify_uid, String name, String phone, String description,String category, double weight, String pickup, String destination, String timestamp, double price, String status) {
        this.uid = uid;
        this.notify_uid = notify_uid;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.category = category;
        this.weight = weight;
        this.pickup = pickup;
        this.destination = destination;
        this.timestamp = timestamp;
        this.price = price;
        this.status = status;
    }

    public String getNotify_uid() {
        return notify_uid;
    }

    public void setNotify_uid(String notify_uid) {
        this.notify_uid = notify_uid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
