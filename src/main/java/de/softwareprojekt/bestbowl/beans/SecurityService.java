package de.softwareprojekt.bestbowl.beans;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

/**
 * Class for managing security.
 *
 * @author Ali
 */
@Component
public class SecurityService {
    private static final String LOGOUT_SUCCESS_URL = "/";

    /**
     * Getter for authenticated users details
     *
     * @return current users {@code UserDetails} or {@code null}
     */
    public UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails;
        }
        // Anonymous or no authentication.
        return null;
    }

    /**
     * Checks if the current user is in the given role
     *
     * @param role role to be checked
     * @return if the user has the role
     */
    public boolean isCurrentUserInRole(String role) {
        UserDetails userDetails = getAuthenticatedUser();
        if (userDetails == null) {
            return false;
        }
        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
            String userAuthority = grantedAuthority.getAuthority().toLowerCase();
            if (userAuthority.contains(role.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Logs out the current user and redirects to the login page
     */
    public void logout() {
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }
}