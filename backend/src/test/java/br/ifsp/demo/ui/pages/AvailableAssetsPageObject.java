package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class AvailableAssetsPageObject extends BasePageObject {
    private static final String PAGE_PATH = "/assets";

    private final By assetCards = By.className("asset-card");
    private final By loadingMessage = By.xpath("//p[contains(text(),'Carregando ativos')]");
    private final By errorMessage = By.xpath("//p[contains(text(),'Falha ao buscar os ativos.')]");
    private final By noAssetsFoundMessage = By.xpath("//p[contains(text(),'Nenhum ativo encontrado.')]");
    private final By investButton = By.className("invest-button");

    private final By dialogTitle = By.xpath("//h2[text()='Registrar um investimento']");
    private final By initialValueInput = By.id("initialValue");
    private final By registerInvestmentButton = By.cssSelector(".MuiDialogActions-root .MuiButton-root:nth-child(2)");
    private final By cancelInvestmentButton = By.cssSelector(".MuiDialogActions-root .MuiButton-root:nth-child(1)");
    private final By snackbarMessage = By.cssSelector(".MuiAlert-message");
    private final By dialogBody = By.cssSelector("[role='dialog']");


    public AvailableAssetsPageObject(WebDriver driver) {
        super(driver);
        this.wait.until(ExpectedConditions.urlContains(PAGE_PATH));
        By assetsGrid = By.className("assets-grid");
        this.wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(assetsGrid),
                ExpectedConditions.visibilityOfElementLocated(loadingMessage),
                ExpectedConditions.visibilityOfElementLocated(errorMessage),
                ExpectedConditions.visibilityOfElementLocated(noAssetsFoundMessage)
        ));
    }

    public boolean isLoadingMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(loadingMessage)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isNoAssetsFoundMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(noAssetsFoundMessage)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public List<WebElement> getAssetCards() {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(assetCards));
    }

    public void clickInvestButtonForAsset(int index) {
        List<WebElement> cards = getAssetCards();
        if (index >= 0 && index < cards.size()) {
            WebElement card = cards.get(index);
            WebElement button = card.findElement(investButton);
            wait.until(ExpectedConditions.elementToBeClickable(button)).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(dialogTitle));
        } else {
            throw new IllegalArgumentException("Asset card index out of bounds. Found " + cards.size() + " cards.");
        }
    }

    public String getDialogTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(dialogTitle)).getText();
    }

    public void enterInitialValue(String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(initialValueInput));
        element.clear();
        element.sendKeys(value);
    }

    public void clickRegisterInvestmentButton() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(registerInvestmentButton));
        element.click();
    }

    public void clickCancelInvestmentButton() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(cancelInvestmentButton));
        element.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(dialogTitle));
    }

    public boolean isDialogClosed() {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(dialogTitle));
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String getSnackbarMessage() {
        WebElement snackbar = wait.until(ExpectedConditions.visibilityOfElementLocated(snackbarMessage));
        return snackbar.getText();
    }

    public boolean isSnackbarDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(snackbarMessage));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}