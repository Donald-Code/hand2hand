package tshabalala.bongani.courierservice.model;

public class Locations {

    private String name;
    private String created_date;
    private String parcel_uid;
    private String user_uid;
    private double latitude;
    private double longitude;
    private String parcel_status;

    public Locations(String name, String created_date, String parcel_uid, String user_uid, double latitude, double longitude,String parcel_status) {
        this.name = name;
        this.created_date = created_date;
        this.parcel_uid = parcel_uid;
        this.user_uid = user_uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parcel_status = parcel_status;
    }

    public String getParcel_status() {
        return parcel_status;
    }

    public void setParcel_status(String parcel_status) {
        this.parcel_status = parcel_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getParcel_uid() {
        return parcel_uid;
    }

    public void setParcel_uid(String parcel_uid) {
        this.parcel_uid = parcel_uid;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
