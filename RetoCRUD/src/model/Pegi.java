package model;

/**
 * Enumeration representing the Pan European Game Information (PEGI) age ratings.
 * These ratings are used to classify video games based on their content suitability
 * for different age groups in accordance with European standards.
 *
 * @author ema
 */
public enum Pegi {
    /**
     * Suitable for all ages.
     * Content is considered suitable for all persons.
     */
    PEGI3,
    
    /**
     * Suitable for ages 6 and older.
     * May contain very mild violence in a comical context.
     */
    PEGI6,
    
    /**
     * Suitable for ages 12 and older.
     * May contain mild violence, mild bad language, or mild suggestive themes.
     */
    PEGI12,
    
    /**
     * Suitable for ages 16 and older.
     * May contain realistic violence, strong language, sexual content, or gambling references.
     */
    PEGI16,
    
    /**
     * Suitable for ages 18 and older.
     * May contain extreme violence, graphic sexual content, gambling, or drug use.
     */
    PEGI18,
    
    /**
     * Default or unspecified PEGI rating.
     * Used when a rating has not been assigned or is unknown.
     */
    DEFAULT
}