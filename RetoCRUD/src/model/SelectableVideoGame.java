package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.time.LocalDate;

public class SelectableVideoGame
{
    private final VideoGame videoGame;
    private final BooleanProperty selected;

    public SelectableVideoGame(VideoGame videoGame, boolean initialSelected)
    {
        this.videoGame = videoGame;
        this.selected = new SimpleBooleanProperty(initialSelected);
    }

    public VideoGame getVideoGame()
    {
        return videoGame;
    }

    public BooleanProperty selectedProperty()
    {
        return selected;
    }

    public boolean isSelected()
    {
        return selected.get();
    }

    public void setSelected(boolean value)
    {
        selected.set(value);
    }

    public int getV_id()
    {
        return videoGame.getV_id();
    }

    public String getV_name()
    {
        return videoGame.getV_name();
    }

    public LocalDate getV_release()
    {
        return videoGame.getV_release();
    }

    public Platform getV_platform()
    {
        return videoGame.getV_platform();
    }

    public Pegi getV_pegi()
    {
        return videoGame.getV_pegi();
    }
}