package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Random;

public class InventoryPage extends BasePage {

    @FindBy(className = "shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(css = "button[class*='btn_inventory']")
    private List<WebElement> addToCartButtons;

    public InventoryPage(WebDriver driver) {
            super(driver);
    }

    public void addRandomProductToCart() {
        // La lista puede estar vacía si el inventario aún no ha cargado:
        // nextInt(0) lanzaría IllegalArgumentException. Esperar a que haya
        // elementos antes de elegir uno al azar.
        wait.until(d -> !addToCartButtons.isEmpty());
        int randomIndex = new Random().nextInt(addToCartButtons.size());
        addToCartButtons.get(randomIndex).click();
    }

    public void addProductToCartByIndex(int index) {
        addToCartButtons.get(index).click();
    }

    public CartPage goToCart() {
        cartIcon.click();
        CartPage cartPage = new CartPage(driver);
        // Espera activa a que la página del carrito cargue antes de devolver
        // el objeto de página; evita que el test opere sobre la vista anterior.
        cartPage.getCartItemsCount();
        return cartPage;
    }

}
