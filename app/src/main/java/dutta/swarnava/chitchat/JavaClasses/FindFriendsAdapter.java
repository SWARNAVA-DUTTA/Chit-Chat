package dutta.swarnava.chitchat.JavaClasses;



public class FindFriendsAdapter {
    private String Name,About,imageUrl;
    public FindFriendsAdapter(){}
    public FindFriendsAdapter(String Name, String About, String imageUrl) {
        this.Name = Name;
        this.About = About;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
}
