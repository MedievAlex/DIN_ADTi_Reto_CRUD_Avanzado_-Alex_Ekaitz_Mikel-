package model;

import java.time.LocalDate;
import java.util.Date;

/**
 * Abstract base class representing a user profile in the system. This class
 * defines the common attributes and behavior shared by all types of user
 * profiles, including both regular users and administrators.
 *
 * Profile serves as the foundation for user identity management and provides
 * the core personal information storage and retrieval functionality for the
 * application.
 *
 * @author Kevin, Alex, Victor, Ekaitz
 */
public class VideoGame {

    private int v_id;
    private String v_name;
    private LocalDate v_release;
    private Platform v_platform;
    private Pegi v_pegi;
    private boolean checked;

    public VideoGame() {
        this.v_id = 0;
        this.v_name = "";
        this.v_release = LocalDate.now();
        this.v_platform = Platform.DEFAULT;
        this.v_pegi = Pegi.DEFAULT;
        this.checked = false;
    }

    public VideoGame(int v_id, String v_name, LocalDate v_release, Platform v_platform, Pegi v_pegi) {
        this.v_id = v_id;
        this.v_name = v_name;
        this.v_release = v_release;
        this.v_platform = v_platform;
        this.v_pegi = v_pegi;
        this.checked = false;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "VideoGame{" + "v_id=" + v_id + ", v_name=" + v_name + ", v_release=" + v_release + ", v_platform=" + v_platform + ", v_pegi=" + v_pegi + ", checked=" + checked + '}';
    } 
}
