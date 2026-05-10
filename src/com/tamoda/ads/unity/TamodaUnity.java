package com.tamoda.ads.unity;

import android.app.Activity;
import android.content.Context;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;

// Import khusus versi 3.x
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

@DesignerComponent(
    version = 1,
    description = "Tamoda Unity Ads - Ekstensi Jaringan Iklan Khusus Tamoda (SDK 3.7.5).",
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
        // Pasang Pendengar (Listener) Iklan
        UnityAds.addListener(new TamodaAdListener());
        // Mulai Inisialisasi
        UnityAds.initialize(activity, gameId, testMode);
        InitializationSuccess();
    }

    @SimpleFunction(description = "Tampilkan Iklan (Interstitial / Rewarded). Pastikan statusnya Ready.")
    public void ShowAd(String placementId) {
        if (UnityAds.isReady(placementId)) {
            UnityAds.show(activity, placementId);
        } else {
            AdFailedToShow(placementId, "Iklan belum siap (Not Ready). Coba beberapa saat lagi.");
        }
    }

    @SimpleFunction(description = "Cek apakah iklan sudah siap ditampilkan.")
    public boolean IsReady(String placementId) {
        return UnityAds.isReady(placementId);
    }

    // ==========================================
    // EVENTS (BLOK KUNING DI KODULAR)
    // ==========================================

    @SimpleEvent(description = "Inisialisasi Berhasil dipanggil.")
    public void InitializationSuccess() {
        EventDispatcher.dispatchEvent(this, "InitializationSuccess");
    }

    @SimpleEvent(description = "Iklan berhasil dimuat dan siap ditampilkan.")
    public void AdLoaded(String placementId) {
        EventDispatcher.dispatchEvent(this, "AdLoaded", placementId);
    }

    @SimpleEvent(description = "Iklan gagal ditampilkan atau gagal dimuat.")
    public void AdFailedToShow(String placementId, String errorMessage) {
        EventDispatcher.dispatchEvent(this, "AdFailedToShow", placementId, errorMessage);
    }

    @SimpleEvent(description = "Iklan mulai diputar.")
    public void AdStarted(String placementId) {
        EventDispatcher.dispatchEvent(this, "AdStarted", placementId);
    }

    @SimpleEvent(description = "Video selesai ditonton penuh. Eksekusi Kapsul + Value Time Banks di sini.")
    public void VideoCompleted(String placementId) {
        EventDispatcher.dispatchEvent(this, "VideoCompleted", placementId);
    }

    @SimpleEvent(description = "Video di-skip oleh user. Jangan berikan poin.")
    public void VideoSkipped(String placementId) {
        EventDispatcher.dispatchEvent(this, "VideoSkipped", placementId);
    }

    // ==========================================
    // LISTENER INTERNAL UNITY 3.7.5
    // ==========================================
    private class TamodaAdListener implements IUnityAdsListener {
        @Override
        public void onUnityAdsReady(String placementId) {
            AdLoaded(placementId);
        }

        @Override
        public void onUnityAdsStart(String placementId) {
            AdStarted(placementId);
        }

        @Override
        public void onUnityAdsFinish(String placementId, UnityAds.FinishState state) {
            if (state == UnityAds.FinishState.COMPLETED) {
                VideoCompleted(placementId);
            } else if (state == UnityAds.FinishState.SKIPPED) {
                VideoSkipped(placementId);
            } else if (state == UnityAds.FinishState.ERROR) {
                AdFailedToShow(placementId, "Terjadi kesalahan saat memutar video.");
            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
            AdFailedToShow("Error", message);
        }
    }
}
