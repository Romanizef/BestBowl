package de.softwareprojekt.bestbowl;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import de.softwareprojekt.bestbowl.utils.Utils;
import de.softwareprojekt.bestbowl.views.LoginView;
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

import javax.sql.DataSource;
import java.io.File;

/**
 * @author Marten Voß
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

    @Bean
    public DataSource dataSource() {
        File dbDir = new File(Utils.getWorkingDirPath() + File.separator + "db");
        if (Utils.createDirectoryIfMissing(dbDir)) {
            String dbURL = "jdbc:h2:file:" + dbDir.getAbsolutePath() + File.separator + "bestBowl;TRACE_LEVEL_FILE=0;TRACE_LEVEL_SYSTEM_OUT=1";
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

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/public/**")).permitAll();
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizerA() {
        return web -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/db/**"));
    }

    @Bean
    public UserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
