package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    @FindBy(id = "inventory_container")
    private WebElement inventoryContainer;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public InventoryPage login(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        loginButton.click();
        // Espera a que el inventario termine de renderizar (React) antes de
        // retornar; evita clicks perdidos sobre botones aún no presentes.
        waitForVisible(inventoryContainer);
        return new InventoryPage(driver);
    }

    public String getErrorMessage() {
        return errorMessage.getText();
    }

}
