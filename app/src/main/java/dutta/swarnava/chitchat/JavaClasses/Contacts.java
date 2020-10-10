package dutta.swarnava.chitchat.JavaClasses;

import java.io.Serializable;

public class Contacts  implements Serializable {
    private String Uid;
    private String imageUrl;
    private String Name;
    private String Phone;
    private String About;
    public Contacts(){}


    public Contacts(String Uid,String imageUrl, String name, String phone, String about) {
        this.Uid = Uid;
        this.imageUrl = imageUrl;
        this.Name = name;
        this.Phone = phone;
        this.About=about;

    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}