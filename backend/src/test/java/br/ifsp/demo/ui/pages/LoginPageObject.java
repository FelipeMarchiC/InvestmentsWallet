package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPageObject extends BasePageObject {
    private static final String PAGE_TITLE = "InvestmentsWallet";
    private static final String PAGE_PATH = "/login";

    private final By emailInput = By.id("email");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.cssSelector(".login-button");
    private final By errorMessage = By.className("error-message");
    private final By registerLink = By.linkText("Register here");

    public LoginPageObject(WebDriver driver) {
        super(driver);
        // Ensure we are on the correct page by checking URL (or title)
        this.wait.until(ExpectedConditions.urlContains(PAGE_PATH));
        if (!PAGE_TITLE.equals(getPageTitle())) {
            throw new IllegalStateException("Wrong page title: " + getPageTitle());
        }
    }

    public void enterEmail(String email) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        element.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        element.sendKeys(password);
    }

    public void clickLoginButton() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        element.click();
    }

    public String getErrorMessage() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
        return element.getText();
    }

    public RegisterPageObject clickRegisterLink() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(registerLink));
        element.click();
        return new RegisterPageObject(driver);
    }

    public boolean isErrorMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}