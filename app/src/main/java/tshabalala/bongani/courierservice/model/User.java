package tshabalala.bongani.courierservice.model;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String role;
    private String name;
    private String surname;
    private String idnumber;
    private String gender;
    private String dateofbirth;
    private String age;
    private String email;
    private String password;
    private String phone;
    private String image;
    private String instanceid;

    public User(String uid, String role, String name, String surname, String idnumber,String gender,String dateofbirth,String age, String email, String password, String phone, String image) {
        this.uid = uid;
        this.name = name;
        this.surname = surname;
        this.idnumber = idnumber;
        this.gender = gender;
        this.dateofbirth = dateofbirth;
        this.age = age;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.image = image;

    }

//    public User(String uid, String role, String name, String surname, String idnumber,String gender,String dateofbirth,String age, String email, String password, String phone) {
//        this.uid = uid;
//        this.name = name;
//        this.surname = surname;
//        this.idnumber = idnumber;
//        this.gender = gender;
//        this.dateofbirth = dateofbirth;
//        this.age = age;
//        this.email = email;
//        this.password = password;
//        this.phone = phone;
//        this.role = role;
//
//    }
    //   final User currentuser = new User(user.getRole(), user.getName(), user.getSurname(), user.getIdnumber(),user.getGender(),user.getDateofbirth(),user.getAge(), user.getEmail(), user.getPassword(), user.getPhone(), downloadUri.toString(),instanceid);
    //
//{password=bon123@T, instanceid=, age=25, role=Customer, image=https://firebasestorage.googleapis.com/v0/b/courier-service-cb608.appspot.com/o/Profile_Images%2F298933?alt=media&token=1acc3854-0d8a-4c95-8c1f-fd05a747b2b7, dateofbirth=20/08/1994, surname=Yahvr, phone=0836496499, name=Tahev, email=thabo@gmail.com, gender=Male, idnumber=9408205649081}
    public User(String uid, String role, String name, String surname, String idnumber,String gender,String dateofbirth,String age, String email, String password, String phone) {
        this.uid = uid;
        this.name = name;
        this.surname = surname;
        this.idnumber = idnumber;
        this.gender = gender;
        this.dateofbirth = dateofbirth;
        this.age = age;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }
         //new User(role, name, surname, idnumber,gender,dob,age, email, password, phone));
    public User(String role, String name, String surname, String idnumber,String gender,String dateofbirth,String age, String email, String password, String phone) {
        this.name = name;
        this.surname = surname;
        this.idnumber = idnumber;
        this.gender = gender;
        this.dateofbirth = dateofbirth;
        this.age = age;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    public User() {

    }

    public String getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(String instanceid) {
        this.instanceid = instanceid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
