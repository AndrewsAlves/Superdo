package com.andysapps.superdo.todo.manager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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
import com.andysapps.superdo.todo.Constants;
import com.andysapps.superdo.todo.events.PremiumPurchaseUpdateEvent;
import com.andysapps.superdo.todo.model.PurchaseDetails;
import com.andysapps.superdo.todo.model.User;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseManager implements PurchasesUpdatedListener{

    public static final String TAG = "PurchaseManager";

    private static PurchaseManager ourInstance;

    public static final String sku_monthly = "superdo_premium_monthy";
    public static final String sku_yearly = "superdo_premium_yearly";

    public SkuDetails skuMonthly;
    public SkuDetails skuYearly;

    private BillingClient billingClient;

    public boolean isUserPremium;

    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    public static PurchaseManager getInstance() {
        return ourInstance;
    }

    private PurchaseManager(Context context) {

        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d(TAG, "onBillingSetupFinished: BillingClient setup finised");
                    querySubscriptions();
                    queryPurchase();
                } else {
                    Log.e(TAG,  billingResult.getDebugMessage());
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });

        acknowledgePurchaseResponseListener = billingResult -> {
            Log.d(TAG, "PurchaseManager() called with: context = [" + context + "]");
        };

    }

    public static void initialise(Context context) {
        ourInstance = new PurchaseManager(context);
    }

    public void querySubscriptions() {

        Log.d(TAG, "querySubscriptions() called");

        List<String> skuList = new ArrayList<>();
        skuList.add(sku_monthly);
        skuList.add(sku_yearly);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && skuDetailsList != null) {

                Log.d(TAG, "querySubscriptions: query successful");

                for (SkuDetails skuDetails : skuDetailsList) {
                    String sku = skuDetails.getSku();

                    Log.e(TAG, "querySubscriptions: sku deatils" + skuDetails.getTitle());

                    if (sku.equals(sku_monthly)) {
                        skuMonthly = skuDetails;
                    } else if (sku.equals(sku_yearly)) {
                        skuYearly = skuDetails;
                    }

                }

            } else {
                Log.e(TAG,  billingResult.getDebugMessage());
            }

        });
    }

    public void queryPurchase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Purchase.PurchasesResult queryPurchasesSubs = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
                if (queryPurchasesSubs.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    return;
                }

                for (Purchase purchase : queryPurchasesSubs.getPurchasesList()) {
                    if (purchase.getSku().equals(sku_monthly) || purchase.getSku().equals(sku_yearly)) {
                        isUserPremium = true;
                    }
                }
            }
        }).start();
    }

    public void purchaseSubMonthly(Activity activity) {
        Log.d(TAG, "purchaseSubMonthly: Purchase monthy executed");
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuMonthly)
                .build();
        billingClient.launchBillingFlow(activity, flowParams);
    }

    public void purchaseSubYearly(Activity activity) {
        Log.d(TAG, "purchaseSubMonthly: Purchase yearly executed");
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuYearly)
                .build();
        billingClient.launchBillingFlow(activity, flowParams);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

        Log.d(TAG, "onPurchasesUpdated() called with: billingResult = [" + billingResult + "], purchases = [" + purchases + "]");

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

            Log.e(TAG, "handlePurchase: User purchased : " + purchase.getSku());

            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }

            User user = FirestoreManager.getInstance().user;
            if (user.getPurchaseDetails() != null) {
                if (user.getPurchaseDetails().getPurchaseToken().equals(purchase.getPurchaseToken())) {
                    Log.e(TAG, "handlePurchase: Purchase already exits");
                    return;
                }
            }

            PurchaseDetails purchaseDetails = new PurchaseDetails();
            purchaseDetails.setSkyId(purchase.getSku());
            purchaseDetails.setPurchaseToken(purchase.getPurchaseToken());
            purchaseDetails.setOrderId(purchase.getOrderId());
            purchaseDetails.setStatus(purchase.getPurchaseState());
            purchaseDetails.setPurchaseTime(new Date(purchase.getPurchaseTime()));
            user.setPurchaseDetails(purchaseDetails);
            FirestoreManager.getInstance().updateUser(user);

            EventBus.getDefault().post(new PremiumPurchaseUpdateEvent(Constants.PURCHASED));


        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            Log.e(TAG, "handlePurchase: User purchase pending : " + purchase.getSku());
            EventBus.getDefault().post(new PremiumPurchaseUpdateEvent(Constants.PENDING));
        } else {
            Log.e(TAG, "handlePurchase: User purchase status unspecified : " + purchase.getSku());
            EventBus.getDefault().post(new PremiumPurchaseUpdateEvent(Constants.UNSPECIFIED));
        }

    }
}
