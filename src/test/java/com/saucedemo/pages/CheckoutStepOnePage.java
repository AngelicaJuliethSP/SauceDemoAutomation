package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutStepOnePage extends BasePage {

    @FindBy(id = "first-name")
    private WebElement firstNameInput;

    @FindBy(id = "last-name")
    private WebElement lastNameInput;

    @FindBy(id = "postal-code")
    private WebElement postalCodeInput;

    @FindBy(id = "continue")
    private WebElement continueButton;

    public CheckoutStepOnePage(WebDriver driver) {
        super(driver);
    }

    public CheckoutStepTwoPage fillPersonalInfo(String firstName, String lastName, String postalCode) {
        waitForVisible(firstNameInput).sendKeys(firstName);
        waitForVisible(lastNameInput).sendKeys(lastName);
        waitForVisible(postalCodeInput).sendKeys(postalCode);

        waitForVisible(continueButton).click();

        return new CheckoutStepTwoPage(driver);
    }
}
