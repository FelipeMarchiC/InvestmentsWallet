package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.pages.RegisterPageObject;
import br.ifsp.demo.ui.utils.BaseSeleniumTest;
import br.ifsp.demo.ui.pages.DashboardPageObject;
import br.ifsp.demo.ui.pages.LoginPageObject;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
@DisplayName("Dashboard Page Tests")
public class DashboardTests extends BaseSeleniumTest {
    private DashboardPageObject dashboardPage;
    private final Faker faker = Faker.instance();

    @BeforeEach
    public void setupDashboardTest() {
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

        dashboardPage = new DashboardPageObject(driver);
    }


    @Test
    @DisplayName("Should display dashboard elements after successful login")
    void shouldDisplayDashboardElementsAfterSuccessfulLogin() {
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");

        assertThat(dashboardPage.getTotalBalance()).isNotNull();
        assertThat(dashboardPage.getExpectedReturn()).isNotNull();
        assertThat(dashboardPage.getTotalInvestmentsCount()).isNotNull();
        assertThat(dashboardPage.isRecentInvestmentsSectionDisplayed()).isTrue();
        assertThat(dashboardPage.isOpportunitiesListDisplayed()).isTrue();
    }


}