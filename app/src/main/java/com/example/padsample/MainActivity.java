package com.example.padsample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.assetpacks.AssetPackLocation;
import com.google.android.play.core.assetpacks.AssetPackManager;
import com.google.android.play.core.assetpacks.AssetPackManagerFactory;
import com.google.android.play.core.assetpacks.AssetPackState;
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener;
import com.google.android.play.core.assetpacks.AssetPackStates;
import com.google.android.play.core.assetpacks.model.AssetPackStatus;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Boolean animated_pack = false;
    private Boolean static_pack = false;
    private static final String TAG = "MainActivity";
    AssetPackManager assetPackManager;
     View animatedCardView, staticCardView;
    static ArrayList<View> viewsList;
    final int mySessionId = 0;
    List<String> list = new ArrayList();
    AssetPackLocation assetPackPath;
    String assetsFolderPath = "";
    String animatedAssetPack = "animated_asset_pack";
    String staticAssetPack = "static_asset_pack";
    String asset_pack = "";
    //-------------------------------------------------------------------
  private void initialize_views(){
      //initialize the  images
      animatedCardView = findViewById(R.id.AnimatedCardView);
      staticCardView = findViewById(R.id.StaticCardView);

      viewsList = new ArrayList<>();
      // adding views to array list
      viewsList.add(animatedCardView);
      viewsList.add(staticCardView);

      for (View view : viewsList) {
          view.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  //Toast.makeText(getApplicationContext(), view.getTag().toString(), Toast.LENGTH_SHORT).show();
                  if (view.getTag().toString().equals("AnimatedCardView")){
                      animated_pack = true;
                      initAssetPackManager();
//                      getAbsoluteAssetPath("animated_asset_pack", "/on_demand/on_demand2.txt");
                  }
                  else if (view.getTag().toString().equals("StaticCardView")) {
                      static_pack = true;
                      initAssetPackManager();
//                      getAbsoluteAssetPath("static_asset_pack", "/fast_follow/fast_follow1.txt");
                  }
              }
          });
      }
  }
    //--------------------------------------------------------------------------------------------------
    private void inputStreamFiles(File file) {

            try {
                InputStream is = new FileInputStream(file);
                int length = is.available();
                byte[] buffer = new byte[length];
                is.read(buffer);
                Toast.makeText(getApplicationContext(), new String(buffer), Toast.LENGTH_LONG).show();
                Log.d("puzzle", new String(buffer));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
  //--------------------------------------------------------------------------------------------------
  private void initAnimatedPack(){
      String assetsPath = getAbsoluteAssetPath(asset_pack, "");
      if (assetsPath == null) {
          getPackStates(asset_pack);
      }
      if (assetsPath != null) {
          File file = new File(assetsFolderPath + "/on_demand2.txt");
          inputStreamFiles(file);
      }

  }
  //---------------------------------------------------------------------------------------------------
  private void initStaticPack(){

      String assetsPath = getAbsoluteAssetPath(asset_pack, "");
      if (assetsPath == null) {
          getPackStates(asset_pack);
      }
      if (assetsPath != null) {
          File file = new File(assetsFolderPath + "/fast_follow2.txt");
          inputStreamFiles(file);
      }

  }
    //---------------------------------------------------------------------------------------------------
    /**
     * This method will check which button was clicked & call respective method to get file & play
     */
    private void initClickedAssetPack() {
        if (animated_pack) {
            animated_pack = false;
            initAnimatedPack();
        }
        else if (static_pack) {
            static_pack = false;
            initStaticPack();
        }
    }
//----------------------------------------------------------------------------------------------------
    /**
     * This method will get Instance of AssetPackManager For fast-follow & on-demand deliver mode
     */
    private void initAssetPackManager () {
        if (Utils.isInternetConnected(getApplicationContext())) {
            if (assetPackManager == null) {
                assetPackManager = AssetPackManagerFactory.getInstance(getApplicationContext());
            }
            registerListener();
        } else {
            Utils.showToast(getBaseContext(), "Please Connect to Internet");
        }
    }

  //-----------------------------------------------------------------------------------------------------
    /**
     * This method will check Asset Pack is available on device or not,
     * if not available, it will register listener for it & start downloading by calling
     * fetch method.
     */
    private void registerListener() {

        if (animated_pack){
            asset_pack = animatedAssetPack;
        }
        else {
            asset_pack = staticAssetPack;
        }

        String onDemandAssetPackPath = getAbsoluteAssetPath(asset_pack, "");

        if (onDemandAssetPackPath == null ) {
            assetPackManager.registerListener(mAssetPackStateUpdateListener);
            List<String> assetPackList = new ArrayList<>();
            assetPackList.add(asset_pack);
            assetPackManager.fetch(assetPackList);
        } else {

            initClickedAssetPack();
        }
    }

    //-------------------------------------------------------------------------------------
    /**
     * This method is used to Get download information about asset packs
     */
    private void getPackStates(String assetPackName) {
        assetPackManager.getPackStates(Collections.singletonList(assetPackName))
                .addOnCompleteListener(new OnCompleteListener<AssetPackStates>() {
                    @Override
                    public void onComplete(Task<AssetPackStates> task) {
                        AssetPackStates assetPackStates;
                        try {
                            assetPackStates = task.getResult();
                            AssetPackState assetPackState =
                                    assetPackStates.packStates().get(assetPackName);

                            Log.d("puzzle", "status: " + assetPackState.status() +
                                    ", name: " + assetPackState.name() +
                                    ", errorCode: " + assetPackState.errorCode() +
                                    ", bytesDownloaded: " + assetPackState.bytesDownloaded() +
                                    ", totalBytesToDownload: " + assetPackState.totalBytesToDownload() +
                                    ", transferProgressPercentage: " + assetPackState.transferProgressPercentage());
                        }catch (Exception e){
                            Log.d("MainActivity", e.getMessage());
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //call function for initialization of the views
        initialize_views();

    }

    AssetPackStateUpdateListener mAssetPackStateUpdateListener = new AssetPackStateUpdateListener() {
        @Override
        public void onStateUpdate(AssetPackState assetPackState) {
            switch (assetPackState.status()) {
                case AssetPackStatus.PENDING:
                    Log.i(TAG, "Pending");
                    break;

                case AssetPackStatus.DOWNLOADING:
                    long downloaded = assetPackState.bytesDownloaded();
                    long totalSize = assetPackState.totalBytesToDownload();
                    double percent = 100.0 * downloaded / totalSize;

                    Log.i(TAG, "PercentDone=" + String.format("%.2f", percent));
                    break;

                case AssetPackStatus.TRANSFERRING:
                    // 100% downloaded and assets are being transferred.
                    // Notify user to wait until transfer is complete.
                    break;

                case AssetPackStatus.COMPLETED:
                    // Asset pack is ready to use. Start the game.
                    initClickedAssetPack();
                    break;

                case AssetPackStatus.FAILED:
                    // Request failed. Notify user.
                    Log.e(TAG, String.valueOf(assetPackState.errorCode()));
                    break;

                case AssetPackStatus.CANCELED:
                    // Request canceled. Notify user.
                    break;

                case AssetPackStatus.WAITING_FOR_WIFI:

                    break;

                case AssetPackStatus.NOT_INSTALLED:
                    // Asset pack is not downloaded yet.
                    break;
                case AssetPackStatus.UNKNOWN:
                    Log.wtf(TAG, "Asset pack status unknown");
                    break;
            }        }
    };


    /**
     * lifecycle method to unregister the listener
     */
    @Override
    protected void onDestroy() {
        try{
            super.onDestroy();
            assetPackManager.unregisterListener(mAssetPackStateUpdateListener);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    //-------------------------------------------------------
    // fast-follow 和 on-demand 需要通过AssetPackLocation获取到assetpack的路径，在根据绝对路径来读取资源
    private String getAbsoluteAssetPath(String assetPack, String relativeAssetPath) {

         assetPackPath = assetPackManager.getPackLocation(assetPack);

        if (assetPackPath == null) {
            // asset pack is not ready
            return null;
        }

         assetsFolderPath = assetPackPath.assetsPath();
        // equivalent to: FilenameUtils.concat(assetPackPath.path(), "assets");
        String assetPath = FilenameUtils.concat(assetsFolderPath, relativeAssetPath);
        return assetPath;

    }

}