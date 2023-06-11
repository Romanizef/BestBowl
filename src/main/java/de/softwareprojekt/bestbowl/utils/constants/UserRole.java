package de.softwareprojekt.bestbowl.utils.constants;

/**
 * The UserRole class contains possible user roles.
 * 
 * @author Marten Voß
 */
public class UserRole {
    public static final String ADMIN = "Admin";
    public static final String OWNER = "Geschäftsführer";
    public static final String EMPLOYEE = "Mitarbeiter";

    /**
     * The UserRole function is a constructor that creates an instance of the
     * UserRole class.
     * 
     * @return A userrole object
     */
    private UserRole() {
    }

    /**
     * The getAllValues function returns an array of all the possible values for a
     * given enum.
     * 
     * @return An array of strings
     */
    public static String[] getAllValues() {
        return new String[] { ADMIN, OWNER, EMPLOYEE };
    }
}