package model;

import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.HashMap;

/**
 * Abstract class representing a general profile in the system. Contains common
 * attributes such as username, password, email, and personal information. All
 * profile types (User, Admin) extend this class.
 *
 * @author acer
 */
public abstract class Profile {

    private String username;
    private String password;
    private String email;
    private int userCode;
    private String name;
    private String telephone;
    private String surname;
    private HashMap<String, ArrayList> lists;

    /**
     * Constructs a profile with the specified attributes.
     *
     * @param username
     * @param password
     * @param email
     * @param userCode
     * @param name
     * @param telephone
     * @param surname
     */
    public Profile(String username, String password, String email, int userCode, String name, String telephone, String surname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userCode = userCode;
        this.name = name;
        this.telephone = telephone;
        this.surname = surname;
        defaultList();
    }

    /**
     * Default constructor initializing attributes with default values.
     */
    public Profile() {
        this.username = "";
        this.password = "";
        this.email = "";
        this.userCode = 0;
        this.name = "";
        this.telephone = "";
        this.surname = "";
        defaultList();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public HashMap<String, ArrayList> getLists() {
        return lists;
    }

    public void setLists(HashMap<String, ArrayList> lists) {
        this.lists = lists;
    }

    private void defaultList() {
        this.lists = new HashMap<String, ArrayList>();
        this.lists.put("All Games", new ArrayList<>());
    }

    public boolean newList(String name, ArrayList<VideoGame> videogames) {
        if (!this.lists.containsKey(name)) {
            lists.put(name, videogames);
            return true;
        } else {
            return false;
        }
    }

    public boolean renameList(String oldName, String newName) {
        if (!this.lists.containsKey(newName)) {
            ArrayList<VideoGame> videogames = lists.remove(oldName);
            lists.put(newName, videogames);
            return true;
        } else {
            return false;
        }
    }

    public boolean addGame(String name, VideoGame videogame) {
        ArrayList<VideoGame> list = this.lists.get(name);
        boolean exist = false;
        
        for (int i = 0; i < list.size(); i++) {
            if (videogame.getV_name().equals(list.get(i).getV_name())) {
                exist = true;
            }
        }

        if (!exist) {
            this.lists.get(name).add(videogame);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean removeGame(String name, VideoGame videogame) {
        ArrayList<VideoGame> list = this.lists.get(name);
        boolean exist = false;
        
        for (int i = 0; i < list.size(); i++) {
            if (videogame.getV_name().equals(list.get(i).getV_name())) {
                exist = true;
            }
        }

        if (!exist) {
            this.lists.get(name).add(videogame);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Profile{" + "username=" + username + ", password=" + password + ", email=" + email
                + ", userCode=" + userCode + ", name=" + name + ", telephone=" + telephone + ", surname=" + surname + '}';
    }

    /**
     * Performs login logic for the profile. Must be implemented by subclasses.
     */
    public abstract void logIn();
}
