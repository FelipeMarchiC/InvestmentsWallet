package br.ifsp.demo.ui;

import br.ifsp.demo.ui.utils.BaseSeleniumTest;
import br.ifsp.demo.ui.pages.LoginPageObject;
import br.ifsp.demo.ui.pages.RegisterPageObject;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
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
                .doesNotContain("/login");
    }

    @DisplayName("should not permit to register with invalid field")
    @ParameterizedTest(name = "[{index}] name=''{0}'', lastname=''{1}'', email=''{2}'', password=''{3}'' → campo inválido: {4}")
    @CsvSource({
            // nome vazio
            "'',Doe,john@example.com,Secret123!,name",
            // sobrenome vazio
            "John,'',john@example.com,Secret123!,lastname",
            // email vazio
            "John,Doe,'',Secret123!,email",
            // email sem '@'
            "John,Doe,johndoe.com,Secret123!,email",
            // senha vazia
            "John,Doe,john@example.com,'',password"
    })
    public void shouldNotPermitToRegisterWithInvalidFields(
            String name,
            String lastname,
            String email,
            String password,
            String invalidFieldId
    ) {
        driver.get(baseUrl + "/register");
        wait.until(ExpectedConditions.urlContains("/register"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        RegisterPageObject register = new RegisterPageObject(driver);
        register.enterName(name);
        register.enterLastname(lastname);
        register.enterEmail(email);
        register.enterPassword(password);

        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".register-button[type='submit']")));
        submit.click();

        WebElement invalidInput = driver.findElement(By.id(invalidFieldId));
        String validationMessage = (String)((JavascriptExecutor)driver)
                .executeScript("return arguments[0].validationMessage;", invalidInput);

        assertThat(validationMessage)
                .as("Campo '%s' com valor inválido deveria disparar validação nativa", invalidFieldId)
                .isNotEmpty();
    }


    @Test
    @DisplayName("Should redirect to login when accessing dashboard unauthenticated")
    public void shouldRedirectToLoginWhenAccessingDashboardUnauthenticated() {
        driver.get(baseUrl + "/dashboard");
        wait.until(ExpectedConditions.urlContains("/login"));

        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl)
                .contains("/login");

        assertThat(driver.findElements(By.id("email")))
                .isNotEmpty();
        assertThat(driver.findElements(By.cssSelector(".login-button")))
                .isNotEmpty();
    }
    @Test
    @DisplayName("Should show error message for an Unregistered user login")
    public void shouldShowErrorMessageForAnUnRegisteredUserLogin() {
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.urlContains("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));

        String randomEmail = faker.internet().emailAddress();
        String randomPassword = faker.internet().password(8, 16, true, true);

        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.enterEmail(randomEmail);
        loginPage.enterPassword(randomPassword);

        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".login-button")));
        loginBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("error-message")));
        assertThat(loginPage.isErrorMessageDisplayed())
                .isTrue();

    }

}