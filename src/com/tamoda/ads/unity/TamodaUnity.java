package com.tamoda.ads.unity;

import android.app.Activity;
import android.content.Context;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;

@DesignerComponent(
    version = 1,
    description = "Tamoda Unity Ads - Ekstensi Jaringan Iklan Khusus Tamoda.",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = ""
)
@SimpleObject(external = true)
@UsesPermissions(permissionNames = "android.permission.INTERNET, android.permission.ACCESS_NETWORK_STATE")
@UsesLibraries(libraries = "unity-ads.jar") 
public class TamodaUnity extends AndroidNonvisibleComponent {

    private Activity activity;
    private Context context;

    public TamodaUnity(ComponentContainer container) {
        super(container.$form());
        this.activity = (Activity) container.$context();
        this.context = container.$context();
    }

    @SimpleFunction(description = "Inisialisasi Unity Ads. Panggil ini pertama kali saat Screen Initialize.")
    public void Initialize(String gameId, boolean testMode) {
        UnityAds.initialize(context, gameId, testMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                InitializationSuccess();
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                InitializationFailed(message);
            }
        });
    }

    @SimpleFunction(description = "Muat Iklan (Load Ad) sebelum ditampilkan.")
    public void LoadAd(String placementId) {
        UnityAds.load(placementId, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String id) {
                AdLoaded(id);
            }

            @Override
            public void onUnityAdsFailedToLoad(String id, UnityAds.UnityAdsLoadError error, String message) {
                AdFailedToLoad(id, message);
            }
        });
    }

    @SimpleFunction(description = "Tampilkan Iklan (Interstitial / Rewarded). Pastikan sudah di-load sebelumnya.")
    public void ShowAd(String placementId) {
        UnityAds.show(activity, placementId, new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(String id, UnityAds.UnityAdsShowError error, String message) {
                AdFailedToShow(id, message);
            }

            @Override
            public void onUnityAdsShowStart(String id) {
                AdStarted(id);
            }

            @Override
            public void onUnityAdsShowClick(String id) {
                AdClicked(id);
            }

            @Override
            public void onUnityAdsShowComplete(String id, UnityAds.UnityAdsShowCompletionState state) {
                if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                    VideoCompleted(id);
                } else if (state == UnityAds.UnityAdsShowCompletionState.SKIPPED) {
                    VideoSkipped(id);
                }
            }
        });
    }

    // ==========================================
    // EVENTS (BLOK KUNING DI KODULAR)
    // ==========================================

    @SimpleEvent(description = "Inisialisasi Berhasil.")
    public void InitializationSuccess() {
        EventDispatcher.dispatchEvent(this, "InitializationSuccess");
    }

    @SimpleEvent(description = "Inisialisasi Gagal.")
    public void InitializationFailed(String errorMessage) {
        EventDispatcher.dispatchEvent(this, "InitializationFailed", errorMessage);
    }

    @SimpleEvent(description = "Iklan berhasil dimuat dan siap ditampilkan.")
    public void AdLoaded(String placementId) {
        EventDispatcher.dispatchEvent(this, "AdLoaded", placementId);
    }

    @SimpleEvent(description = "Iklan gagal dimuat.")
    public void AdFailedToLoad(String placementId, String errorMessage) {
        EventDispatcher.dispatchEvent(this, "AdFailedToLoad", placementId, errorMessage);
    }

    @SimpleEvent(description = "Iklan gagal ditampilkan.")
    public void AdFailedToShow(String placementId, String errorMessage) {
        EventDispatcher.dispatchEvent(this, "AdFailedToShow", placementId, errorMessage);
    }

    @SimpleEvent(description = "Iklan mulai diputar.")
    public void AdStarted(String placementId) {
        EventDispatcher.dispatchEvent(this, "AdStarted", placementId);
    }

    @SimpleEvent(description = "Iklan diklik oleh user.")
    public void AdClicked(String placementId) {
        EventDispatcher.dispatchEvent(this, "AdClicked", placementId);
    }

    @SimpleEvent(description = "Video selesai ditonton penuh. Eksekusi Kapsul + Value Time Banks di sini.")
    public void VideoCompleted(String placementId) {
        EventDispatcher.dispatchEvent(this, "VideoCompleted", placementId);
    }

    @SimpleEvent(description = "Video di-skip oleh user. Jangan berikan poin.")
    public void VideoSkipped(String placementId) {
        EventDispatcher.dispatchEvent(this, "VideoSkipped", placementId);
    }
}
