package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutCompletePage extends BasePage {

    @FindBy(css = "[data-test='complete-header']")
    private WebElement thankYouMessage;

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public String getThankYouMessage() {
        // Espera el mensaje: con performance_glitch_user el sitio tarda en
        // renderizar la confirmación y una lectura directa lanza NoSuchElement.
        return waitForVisible(thankYouMessage).getText();
    }
}
