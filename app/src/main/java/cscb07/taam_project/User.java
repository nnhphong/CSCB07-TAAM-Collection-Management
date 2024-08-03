package cscb07.taam_project;

public class User {
    String username, password;

    public User() {
        username = "";
        password = "";
    }
    public User(String username, String pwd) {
        this.username = username;
        password = pwd;
    }

    public String getUsername() {return username;}
    public void setUsername(String usrname) {username = usrname;}
    public String getPassword() {return password;}
    public void setPassword(String pwd) {password = pwd;}
}
