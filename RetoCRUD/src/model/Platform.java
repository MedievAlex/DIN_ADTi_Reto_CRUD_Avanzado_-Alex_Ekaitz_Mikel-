package model;

/**
 * Enumeration representing the available gaming platforms in the system.
 * These platforms are used to categorize video games based on the hardware
 * or system they are designed to run on.
 *
 * @author ema
 */
public enum Platform {
    /**
     * PlayStation gaming platform.
     * Represents games designed for Sony PlayStation consoles.
     */
    PLAYSTATION,
    
    /**
     * Nintendo gaming platform.
     * Represents games designed for Nintendo consoles (Switch, Wii, DS, etc.).
     */
    NINTENDO,
    
    /**
     * Xbox gaming platform.
     * Represents games designed for Microsoft Xbox consoles.
     */
    XBOX,
    
    /**
     * Personal Computer gaming platform.
     * Represents games designed to run on Windows, macOS, or Linux systems.
     */
    PC,
    
    /**
     * Default or unspecified platform.
     * Used when a platform has not been assigned or is unknown.
     */
    DEFAULT
}