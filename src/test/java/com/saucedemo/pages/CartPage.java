package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CartPage extends BasePage {

    @FindBy(className = "cart_item")
    private List<WebElement> cartItems;

    @FindBy(css = "button[class*='cart_button']")
    private List<WebElement> removeButtons;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public void removeAllItems() {
        while (!removeButtons.isEmpty()) {
            removeButtons.get(0).click();
        }
    }

    public int getCartItemsCount() {
        // Espera el contenedor del carrito (existe incluso vacío) antes de
        // contar; evita contar 0 cuando la página aún está renderizando y
        // no bloquea el caso "carrito vacío" tras remover los items.
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("cart_contents_container")));
        return cartItems.size();
    }

    public CheckoutStepOnePage checkout() {
        checkoutButton.click();
        return new CheckoutStepOnePage(driver);
    }
}
