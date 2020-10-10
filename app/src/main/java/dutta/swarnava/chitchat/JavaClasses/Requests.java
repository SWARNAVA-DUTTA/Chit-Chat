package dutta.swarnava.chitchat.JavaClasses;

import java.io.Serializable;

public class Requests implements Serializable
{
    private String Uid;
    private String imageUrl;
    private String Name;
    private String Phone;
    private String About;
    public Requests(){}
    public Requests(String uid, String imageUrl, String name, String phone, String about) {
        Uid = uid;
        this.imageUrl = imageUrl;
        Name = name;
        Phone = phone;
        About = about;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
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

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }
}
