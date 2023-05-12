package de.softwareprojekt.bestbowl.views;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

@SpringBootTest
public class LoginViewTestIT {

    @Test
    void testCheckPageLoad() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get("http://localhost:8080/" + "login");
            new WebDriverWait(driver, Duration.ofSeconds(20), Duration.ofSeconds(1)).until(titleIs("Login"));

            WebElement button = driver.findElement(By.xpath("//vaadin-button[contains(.,'Anmelden')]"));

            Assertions.assertEquals("Anmelden", button.getText());
        } finally {
            driver.quit();
        }
    }
}
