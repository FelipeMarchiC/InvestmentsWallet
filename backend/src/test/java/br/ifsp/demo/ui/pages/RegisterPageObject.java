package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class RegisterPageObject extends BasePageObject {
    private static final String PAGE_TITLE = "InvestmentsWallet"; // Assuming title is same for register
    private static final String PAGE_PATH = "/register";

    private final By nameInput = By.id("name");
    private final By lastnameInput = By.id("lastname");
    private final By emailInput = By.id("email");
    private final By passwordInput = By.id("password");
    private final By registerButton = By.cssSelector(".register-button[type='submit']");
    private final By cancelButton = By.cssSelector(".register-button:nth-of-type(2)");
    private final By errorMessage = By.className("error-message");
    private final By successMessage = By.className("success-message");


    public RegisterPageObject(WebDriver driver) {
        super(driver);
        this.wait.until(ExpectedConditions.urlContains(PAGE_PATH));
        if (!PAGE_TITLE.equals(getPageTitle())) {
            throw new IllegalStateException("Wrong page title: " + getPageTitle());
        }
    }

    public void enterName(String name) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput));
        element.sendKeys(name);
    }

    public void enterLastname(String lastname) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(lastnameInput));
        element.sendKeys(lastname);
    }

    public void enterEmail(String email) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        element.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        element.sendKeys(password);
    }

    public void clickRegisterButton() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(registerButton));
        element.click();
    }

    public LoginPageObject clickCancelButton() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(cancelButton));
        element.click();
        return new LoginPageObject(driver);
    }

    public String getErrorMessage() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
        return element.getText();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String getSuccessMessage() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
        return element.getText();
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}