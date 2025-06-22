package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.utils.BaseSeleniumTest;
import br.ifsp.demo.ui.pages.AvailableAssetsPageObject;
import br.ifsp.demo.ui.pages.LoginPageObject;
import br.ifsp.demo.ui.pages.RegisterPageObject;
import br.ifsp.demo.ui.pages.DashboardPageObject;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
@DisplayName("Available Assets Page Tests")
public class AvailableAssetsTests extends BaseSeleniumTest {
    private AvailableAssetsPageObject availableAssetsPage;
    private final Faker faker = Faker.instance();

    @BeforeEach
    public void setupAssetsTest() {
        String baseUrl = "http://localhost:5173";
        driver.get(baseUrl + "/register");
        wait.until(ExpectedConditions.urlContains("/register"));

        String userEmail = faker.internet().emailAddress();
        String userPassword = faker.internet().password(8, 16, true, true);

        RegisterPageObject registerPage = new RegisterPageObject(driver);
        registerPage.enterName(faker.name().firstName());
        registerPage.enterLastname(faker.name().lastName());
        registerPage.enterEmail(userEmail);
        registerPage.enterPassword(userPassword);
        registerPage.clickRegisterButton();

        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.enterEmail(userEmail);
        loginPage.enterPassword(userPassword);
        loginPage.clickLoginButton();

        wait.until(ExpectedConditions.urlContains("/dashboard"));

        DashboardPageObject dashboardPage = new DashboardPageObject(driver);
        availableAssetsPage = dashboardPage.clickInvestNowButton();
    }

    @Test
    @DisplayName("Should display available asset cards")
    void shouldDisplayAvailableAssetCards() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("assets-grid")));
        assertThat(availableAssetsPage.getAssetCards()).isNotEmpty();
        assertThat(availableAssetsPage.isLoadingMessageDisplayed()).isFalse();
        assertThat(availableAssetsPage.isErrorMessageDisplayed()).isFalse();
        assertThat(availableAssetsPage.isNoAssetsFoundMessageDisplayed()).isFalse();
    }
}