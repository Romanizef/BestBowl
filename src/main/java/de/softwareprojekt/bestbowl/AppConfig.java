package de.softwareprojekt.bestbowl;

import java.io.File;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.views.LoginView;

/**
 * @author Ali
 */
@Configuration
@EnableWebSecurity
public class AppConfig extends VaadinWebSecurity {
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    /**
     * The dataSource function creates a new DataSource object, which is used to
     * connect to the database.
     * The function first checks if the directory for the database exists and
     * creates it if necessary.
     * Then it sets up all properties of the connection pool (e.g., URL, username,
     * password) and returns a new DataSource object with these properties set up.
     *
     * @return A datasource object
     */
    @Bean
    public DataSource dataSource() {
        File dbDir = new File(Utils.getWorkingDirPath() + File.separator + "db");
        if (Utils.createDirectoryIfMissing(dbDir)) {
            String dbURL = "jdbc:h2:file:" + dbDir.getAbsolutePath() + File.separator
                    + "bestBowl;TRACE_LEVEL_FILE=0;TRACE_LEVEL_SYSTEM_OUT=1";
            PoolProperties poolProperties = new PoolProperties();
            poolProperties.setUrl(dbURL);
            poolProperties.setUsername(userName);
            poolProperties.setPassword(password);
            poolProperties.setDriverClassName(driverClassName);
            poolProperties.setTestOnBorrow(true);
            poolProperties.setValidationQuery("SELECT 1");
            poolProperties.setValidationInterval(0);
            poolProperties.setMaxIdle(12);
            poolProperties.setMinIdle(8);
            return new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
        } else {
            return null;
        }
    }

    /**
     * The configure function is used to configure the security of the application.
     * It allows for a custom login view, and also defines which paths are
     * publically accessible.
     * 
     * @param HttpSecurity http Configure the security of the web application
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/public/**")).permitAll();
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    /**
     * The webSecurityCustomizerA function is a WebSecurityCustomizer that ignores
     * all requests to the /db/** path.
     * This allows us to access the H2 database console without having to log in.
     * 
     * @return A websecuritycustomizer
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizerA() {
        return web -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/db/**"));
    }

    /**
     * The userDetailsService function is used to create a UserDetailsManager
     * object.
     * This object is used by the Spring Security framework to manage user accounts.
     * In this case, we are using an in-memory implementation of the
     * UserDetailsManager interface, which means that all user account information
     * will be stored in memory and not persisted anywhere else (e.g., on disk).
     * 
     * @return An instance of userdetailsmanager
     */
    @Bean
    public UserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    /**
     * The passwordEncoder function is used to encode the password of a user.
     * The encoding algorithm is delegated to the PasswordEncoderFactories class,
     * which uses BCrypt by default.
     * 
     * @return A passwordencoder object
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}