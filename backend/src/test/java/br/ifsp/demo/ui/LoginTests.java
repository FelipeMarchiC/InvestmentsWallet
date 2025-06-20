package br.ifsp.demo.ui;

import br.ifsp.demo.ui.utils.BaseSeleniumTest;
import br.ifsp.demo.ui.pages.LoginPageObject;
import br.ifsp.demo.ui.pages.RegisterPageObject;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;
@Tag("UiTest")
@DisplayName("Login and Registration Tests")
public class LoginTests extends BaseSeleniumTest {
    private final String baseUrl = "http://localhost:5173"; // ajuste conforme seu ambiente
    private final Faker faker = Faker.instance();

    @Override
    protected void setInitialPage() {
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
    }

    @Test
    @DisplayName("Should Register a new User and then Authenticate")
    public void shouldRegisterANewUserAndThenAuthenticate() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Register here")));
        LoginPageObject loginPage = new LoginPageObject(driver);
        RegisterPageObject registerPage = loginPage.clickRegisterLink();
        wait.until(ExpectedConditions.urlContains("/register"));

        String name = faker.name().firstName();
        String lastname = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(8, 16, true, true);

        registerPage.enterName(name);
        registerPage.enterLastname(lastname);
        registerPage.enterEmail(email);
        registerPage.enterPassword(password);

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".register-button[type='submit']")));
        registerPage.clickRegisterButton();

        wait.until(ExpectedConditions.urlContains("/login"));
        LoginPageObject loginAfterRegister = new LoginPageObject(driver);

        loginAfterRegister.enterEmail(email);
        loginAfterRegister.enterPassword(password);

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".login-button")));
        loginAfterRegister.clickLoginButton();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
        String currentUrl = loginAfterRegister.getCurrentUrl();
        assertThat(currentUrl)
                .as("Após login, deveria redirecionar para área autenticada")
                .doesNotContain("/login");
    }
}