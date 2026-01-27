package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.time.LocalDate;

/**
 * Wrapper class that adds selectable functionality to a VideoGame.
 * This class is designed for use with JavaFX TableView to allow users
 * to select multiple games using checkboxes.
 * It extends the VideoGame functionality by adding a boolean property
 * that can be bound to UI controls.
 *
 * @author ema
 */
public class SelectableVideoGame
{
    /**
     * The underlying VideoGame object containing the game data.
     */
    private final VideoGame videoGame;
    
    /**
     * JavaFX property representing the selection state of this game.
     * Can be bound to checkboxes in a TableView.
     */
    private final BooleanProperty selected;

    /**
     * Constructs a new SelectableVideoGame with the specified video game
     * and initial selection state.
     *
     * @param videoGame The VideoGame object to wrap
     * @param initialSelected The initial selection state (true if selected, false otherwise)
     */
    public SelectableVideoGame(VideoGame videoGame, boolean initialSelected)
    {
        this.videoGame = videoGame;
        this.selected = new SimpleBooleanProperty(initialSelected);
    }

    /**
     * Gets the underlying VideoGame object.
     *
     * @return The wrapped VideoGame object
     */
    public VideoGame getVideoGame()
    {
        return videoGame;
    }

    /**
     * Gets the BooleanProperty representing the selection state.
     * This property can be bound to UI controls.
     *
     * @return The BooleanProperty for selection
     */
    public BooleanProperty selectedProperty()
    {
        return selected;
    }

    /**
     * Gets the current selection state.
     *
     * @return true if the game is selected, false otherwise
     */
    public boolean isSelected()
    {
        return selected.get();
    }

    /**
     * Sets the selection state.
     *
     * @param value true to select the game, false to deselect it
     */
    public void setSelected(boolean value)
    {
        selected.set(value);
    }

    /**
     * Gets the ID of the video game.
     * Delegates to the underlying VideoGame object.
     *
     * @return The video game ID
     */
    public int getV_id()
    {
        return videoGame.getV_id();
    }

    /**
     * Gets the name of the video game.
     * Delegates to the underlying VideoGame object.
     *
     * @return The video game name
     */
    public String getV_name()
    {
        return videoGame.getV_name();
    }

    /**
     * Gets the release date of the video game.
     * Delegates to the underlying VideoGame object.
     *
     * @return The release date
     */
    public LocalDate getV_release()
    {
        return videoGame.getV_release();
    }

    /**
     * Gets the platform of the video game.
     * Delegates to the underlying VideoGame object.
     *
     * @return The gaming platform
     */
    public Platform getV_platform()
    {
        return videoGame.getV_platform();
    }

    /**
     * Gets the PEGI rating of the video game.
     * Delegates to the underlying VideoGame object.
     *
     * @return The PEGI rating
     */
    public Pegi getV_pegi()
    {
        return videoGame.getV_pegi();
    }
}