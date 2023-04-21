package de.softwareprojekt.bestbowl.utils.enums;

/**
 * @author Marten Voß
 */
public class UserRole {
    public static final String ADMIN = "admin";
    public static final String OWNER = "owner";
    public static final String EMPLOYEE = "employee";

    private UserRole() {
    }

    /**
     * @return a String Array containing all role values
     */
    public static String[] getAllValues() {
        return new String[]{ADMIN, OWNER, EMPLOYEE};
    }
}
