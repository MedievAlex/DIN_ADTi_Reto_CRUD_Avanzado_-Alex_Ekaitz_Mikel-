package model;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "profile_")
@Check(constraints = "TELEPHONE REGEXP '^[0-9]{9}$'")
public abstract class Profile implements Serializable
{
    @Id
    @Column(name = "username", length = 40)
    private String username;

    @Column(name = "password_", length = 40)
    private String password;

    @Column(name = "email", unique = true, length = 40)
    private String email;

    @Column(name = "name_", length = 40)
    private String name;

    @Column(name = "telephone", length = 9)
    private String telephone;

    @Column(name = "surname", length = 40)
    private String surname;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Listed> listedGames = new HashSet<>();

    @Transient
    private HashMap<String, ArrayList<VideoGame>> lists;

    public Profile() {}

    public Profile(String username, String password, String email, String name, String telephone, String surname)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.telephone = telephone;
        this.surname = surname;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public Set<Listed> getListedGames() { return listedGames; }
    public void setListedGames(Set<Listed> listedGames) { this.listedGames = listedGames; }

    public HashMap<String, ArrayList<VideoGame>> getLists()
    {
        if (lists == null) buildListsFromListed();
        return lists;
    }

    private void buildListsFromListed()
    {
        lists = new HashMap<>();
        for (Listed listed : listedGames)
        {
            String listName = listed.getListName();
            VideoGame game = listed.getVideogame();
            lists.computeIfAbsent(listName, k -> new ArrayList<>()).add(game);
        }
        lists.computeIfAbsent("My Games", k -> new ArrayList<>());
    }

    public boolean newList(String listName)
    {
        if (getLists().containsKey(listName)) return false;
        getLists().put(listName, new ArrayList<>());
        return true;
    }

    public boolean renameList(String oldName, String newName)
    {
        HashMap<String, ArrayList<VideoGame>> memLists = getLists();
        if (!memLists.containsKey(oldName) || memLists.containsKey(newName)) return false;

        ArrayList<VideoGame> games = memLists.remove(oldName);
        memLists.put(newName, games);

        for (Listed listed : listedGames)
        {
            if (listed.getListName().equals(oldName))
            {
                listed.setListName(newName);
            }
        }
        return true;
    }

    public boolean addGame(String listName, VideoGame game)
    {
        List<VideoGame> games = getLists().computeIfAbsent(listName, k -> new ArrayList<>());
        for (VideoGame g : games)
        {
            if (g.getV_id() == game.getV_id()) return false;
        }

        games.add(game);

        Listed listed = new Listed(this, game, listName);
        listedGames.add(listed);
        return true;
    }

    public boolean removeGame(String listName, VideoGame game)
    {
        List<VideoGame> games = getLists().get(listName);
        if (games == null) return false;

        boolean removed = games.removeIf(g -> g.getV_id() == game.getV_id());
        if (removed) {
            listedGames.removeIf(l -> l.getListName().equals(listName) && l.getVideogame().getV_id() == game.getV_id());
        }
        return removed;
    }

    @Override
    public String toString()
    {
        return "Profile{" + "username=" + username + ", email=" + email + ", name=" + name +
                ", telephone=" + telephone + ", surname=" + surname + '}';
    }

    public abstract String show();
}
