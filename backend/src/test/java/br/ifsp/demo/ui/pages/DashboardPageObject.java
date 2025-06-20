package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DashboardPageObject extends BasePageObject {
    private static final String PAGE_PATH = "/dashboard";

    private final By totalBalanceValue = By.cssSelector(".new-info-box.new-highlight-blue .new-info-value");
    private final By expectedReturnValue = By.cssSelector(".new-info-box.new-highlight-green .new-info-value");
    private final By totalInvestmentsCount = By.cssSelector(".new-info-box.new-highlight-purple .new-info-value");
    private final By recentInvestmentsSection = By.cssSelector(".new-recent-investments-card");
    private final By noRecentInvestmentsMessage = By.cssSelector(".new-recent-investments-card .new-empty-message");
    private final By investNowButton = By.cssSelector(".invest-now-button");
    private final By opportunitiesList = By.cssSelector(".new-opportunities-list");
    private final By opportunityItemTitle = By.cssSelector(".new-opportunity-item-title");

    public DashboardPageObject(WebDriver driver) {
        super(driver);
        this.wait.until(ExpectedConditions.urlContains(PAGE_PATH));
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".new-dashboard-container")));
    }

    public String getTotalBalance() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(totalBalanceValue))
                .getText();
    }

    public String getExpectedReturn() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(expectedReturnValue))
                .getText();
    }

    public String getTotalInvestmentsCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(totalInvestmentsCount))
                .getText();
    }

    public boolean isRecentInvestmentsSectionDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(recentInvestmentsSection))
                .isDisplayed();
    }

    public String getNoRecentInvestmentsMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(noRecentInvestmentsMessage)).
                getText();
    }

    public AvailableAssetsPageObject clickInvestNowButton() {
        wait.until(ExpectedConditions.elementToBeClickable(investNowButton)).click();
        return new AvailableAssetsPageObject(driver);
    }

    public boolean isOpportunitiesListDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(opportunitiesList))
                .isDisplayed();
    }

    public String getFirstOpportunityTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(opportunityItemTitle))
                .getText();
    }
}