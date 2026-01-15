package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author ema
 */
@Entity
@Table(name = "videogame")
public class VideoGame implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "v_id")
    private int v_id;

    @Column(name = "v_name", nullable = false, length = 100)
    private String v_name;

    @Column(name = "v_release")
    private LocalDate v_release;

    @Enumerated(EnumType.STRING)
    @Column(name = "v_platform")
    private Platform v_platform;

    @Enumerated(EnumType.STRING)
    @Column(name = "v_pegi")
    private Pegi v_pegi;

    @OneToMany(mappedBy = "videogame", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Listed> listedInProfiles = new HashSet<>();

    public VideoGame() {
        this.v_name = "";
        this.v_release = LocalDate.now();
        this.v_platform = Platform.DEFAULT;
        this.v_pegi = Pegi.DEFAULT;
    }

    public VideoGame(String v_name, LocalDate v_release, Platform v_platform, Pegi v_pegi) {
        this.v_name = v_name;
        this.v_release = v_release;
        this.v_platform = v_platform;
        this.v_pegi = v_pegi;
    }

    public VideoGame(int v_id, String v_name, LocalDate v_release, Platform v_platform, Pegi v_pegi) {
        this.v_id = v_id;
        this.v_name = v_name;
        this.v_release = v_release;
        this.v_platform = v_platform;
        this.v_pegi = v_pegi;
    }

    public int getV_id() {
        return v_id;
    }

    public void setV_id(int v_id) {
        this.v_id = v_id;
    }

    public String getV_name() {
        return v_name;
    }

    public void setV_name(String v_name) {
        this.v_name = v_name;
    }

    public LocalDate getV_release() {
        return v_release;
    }

    public void setV_release(LocalDate v_release) {
        this.v_release = v_release;
    }

    public Platform getV_platform() {
        return v_platform;
    }

    public void setV_platform(Platform v_platform) {
        this.v_platform = v_platform;
    }

    public Pegi getV_pegi() {
        return v_pegi;
    }

    public void setV_pegi(Pegi v_pegi) {
        this.v_pegi = v_pegi;
    }

    public Set<Listed> getListedInProfiles() {
        return listedInProfiles;
    }

    public void setListedInProfiles(Set<Listed> listedInProfiles) {
        this.listedInProfiles = listedInProfiles;
    }

    @Override
    public String toString() {
        return "VideoGame{"
                + "v_id=" + v_id
                + ", v_name='" + v_name + '\''
                + ", v_release=" + v_release
                + ", v_platform=" + v_platform
                + ", v_pegi=" + v_pegi
                + "}";
    }
}
