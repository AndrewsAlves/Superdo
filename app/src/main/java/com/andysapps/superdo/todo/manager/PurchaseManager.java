package com.andysapps.superdo.todo.manager;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class PurchaseManager implements PurchasesUpdatedListener{

    private static PurchaseManager ourInstance;

    public static final String sku_monthly = "superdo_monthy";
    public static final String sku_yearly = "superdo_yearly";

    SkuDetails skuMonthly;
    SkuDetails skuYearly;

    private BillingClient billingClient;

    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    public static PurchaseManager getInstance() {
        return ourInstance;
    }

    private PurchaseManager(Context context) {

        billingClient = BillingClient.newBuilder(context).setListener(this).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    querySubscriptions();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });

        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

            }
        };

    }

    public static void initialise(Context context) {
        ourInstance = new PurchaseManager(context);
    }

    public void querySubscriptions() {

        List<String> skuList = new ArrayList<>();
        skuList.add(sku_monthly);
        skuList.add(sku_yearly);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && skuDetailsList != null) {

                for (SkuDetails skuDetails : skuDetailsList) {
                    String sku = skuDetails.getSku();

                    if (sku.equals(sku_monthly)) {
                        skuMonthly = skuDetails;
                    } else if (sku.equals(sku_yearly)) {
                        skuYearly = skuDetails;
                    }

                }

            }

        });
    }

    public void purchaseSubMonthly(Activity activity) {

        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuMonthly)
                .build();
        billingClient.launchBillingFlow(activity, flowParams);

    }

    public void purchaseSubYearly(Activity activity) {

        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuYearly)
                .build();
        billingClient.launchBillingFlow(activity, flowParams);

    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {

        }
    }

    public void handlePurchase(Purchase purchase) {

        // once purchased acknowledge the purchase

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.

            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {

        }

    }
}
