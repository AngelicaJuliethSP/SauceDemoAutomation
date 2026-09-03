package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Listeners(com.saucedemo.listeners.ScreenshotListener.class)

public class PurchaseTest extends BaseTest{

    @Test
    public void purchaseProductSuccessfully() {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");

        inventoryPage.addRandomProductToCart();
        CartPage cartPage = inventoryPage.goToCart();

        CheckoutStepOnePage stepOnePage = cartPage.checkout();
        CheckoutStepTwoPage stepTwoPage = stepOnePage.fillPersonalInfo("Angelica", "Silva", "110110");
        CheckoutCompletePage completePage = stepTwoPage.finish();

        Assert.assertEquals(completePage.getThankYouMessage(), "Thank you for your order!",
                "The confirmation message was not displayed correctly.");
    }

}
