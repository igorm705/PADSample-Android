package com.example.padsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Boolean animated_pack = false;
    private Boolean static_pack = false;
    private static final String TAG = "MainActivity";
    AssetPackManager assetPackManager;
    View animatedCardView, staticCardView;
    static ArrayList<View> viewsList;
    List<String> list = new ArrayList();
    AssetPackLocation assetPackPath;
    static String assetsFolderPath = "";
    String animatedAssetPack = "animated_asset_pack";
    String staticAssetPack = "static_asset_pack";
    String asset_pack = "";
    ArrayList<StickerPack> stickerPackList = new ArrayList<>();
    JSONObject jo;
    JSONArray stickers;
    Iterator<JSONObject> iterator;
    HashMap<String, JSONObject> identifierSet = new HashMap<String, JSONObject>();
    byte[] bytes;


    //-------------------------------------------------------------------
  private void initialize_views(){


      Log.i("RecyclerView", "class MainActivity, " +
              "   private void initialize_views() ");

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

                      try {
                          initAssetPackManager();
                      } catch (IOException | ParseException e) {
                          e.printStackTrace();
                      }
//                      getAbsoluteAssetPath("animated_asset_pack", "/on_demand/on_demand2.txt");
                  }
                  else if (view.getTag().toString().equals("StaticCardView")) {
                      static_pack = true;

                      try {
                          initAssetPackManager();
                      } catch (IOException | ParseException e) {
                          e.printStackTrace();
                      }
//                      getAbsoluteAssetPath("static_asset_pack", "/fast_follow/fast_follow1.txt");
                  }
              }
          });
      }
  }
  //---------------------------------------------------------------------------------------------------
  @NonNull
  private  ArrayList<StickerPack> fetchFromJSON(JSONObject jo) {

      Log.i("RecyclerView", "class MainActivity, " +
              "    private  ArrayList<StickerPack> fetchFromJSON(JSONObject jo) ");

      ArrayList<StickerPack> stickerPackList = new ArrayList<>();

      // A JSON array. JSONObject supports java.util.List interface.
      JSONArray stickersJSON =  new JSONArray();
      stickersJSON = (JSONArray) jo.get("sticker_packs");

      // Iterate jsonArray using for loop
      for (int i = 0; i < stickersJSON.size(); i++) {
          JSONObject jo_inner = new JSONObject();
          jo_inner =(JSONObject) stickersJSON.get(i);

          final String ANIMATED_STICKER_PACK = "animated_sticker_pack";
          final String identifier = (String) jo_inner.get("identifier");
          final String name = (String) jo_inner.get("name");
          final String publisher = (String) jo_inner.get("publisher");
          final String trayImage = (String) jo_inner.get("tray_image_file");
          final String androidPlayStoreLink = (String) jo_inner.get("android_play_store_link");
          final String iosAppLink = (String) jo_inner.get("ios_app_download_link");
          final String publisherEmail = (String) jo_inner.get("publisher_email");
          final String publisherWebsite = (String) jo_inner.get("publisher_website");
          final String privacyPolicyWebsite = (String) jo_inner.get( "privacy_policy_website");
          final String licenseAgreementWebsite = (String) jo_inner.get("license_agreement_website");
          final String imageDataVersion = (String) jo_inner.get("image_data_version");

          boolean avoidCache = false;
          boolean animatedStickerPack = false;
          try {
              animatedStickerPack = (Boolean) jo_inner.get ("animatedStickerPack");
          }
          catch (Exception e) {
              Log.e(TAG, "animatedStickerPack is false");
          }


          final StickerPack stickerPack = new StickerPack(identifier, name, publisher, trayImage, publisherEmail, publisherWebsite, privacyPolicyWebsite, licenseAgreementWebsite, imageDataVersion, avoidCache, animatedStickerPack);
          stickerPack.setAndroidPlayStoreLink(androidPlayStoreLink);
          stickerPack.setIosAppStoreLink(iosAppLink);

          // Mapping JSONObjects values to string keys
          identifierSet.put(identifier, jo_inner);
          stickerPackList.add(stickerPack);
      }


      return stickerPackList;
  }
    //--------------------------------------------------------------------------------------------------
    private void inputStreamFiles(File file) {


        Log.i("RecyclerView", "class MainActivity, " +
                "    private void inputStreamFiles(File file) ");

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
    //------------------------------------------------------------------------------------------------
    static public String getAssetsFolderPath (String identifier, String name){

        Log.i("RecyclerView", "class MainActivity, " +
                "   static public String getAssetsFolderPath (String identifier, String name) ");

      String str = assetsFolderPath + "/" + identifier + "/" + name;

      return str;
    }
    //------------------------------------------------------------------------------------------------
    @NonNull
    private  List<Sticker> fetchFromContentProviderForStickers(StickerPack stickerPack) {

        Log.i("RecyclerView", "class MainActivity, " +
                "   private  List<Sticker> fetchFromContentProviderForStickers(StickerPack stickerPack)  ");

        //Uri uri = getStickerListUri(identifier);

   //     final String[] projection = {"sticker_file_name", "sticker_emoji"};
      //  final Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        List<Sticker> stickers = new ArrayList<>();


        // A JSON array. JSONObject supports java.util.List interface.
        JSONObject jo_inner = new JSONObject();
        jo_inner = identifierSet.get(stickerPack.identifier);

        JSONArray stickersJSON =  new JSONArray();
        stickersJSON = (JSONArray) jo_inner.get("stickers");

        // Iterate jsonArray using for loop
        for (int i = 0; i < stickersJSON.size(); i++) {
                JSONObject jo_temp = new JSONObject();
                jo_temp =(JSONObject) stickersJSON.get(i);
            final String name = (String) jo_temp.get("image_file");
            List<String> emojis = new ArrayList();
            emojis = (List<String>) jo_temp.get("emojis");
            stickers.add(new Sticker(name, emojis));
        }

        return stickers;
    }
    //--------------------------------------------------------------------------------------------------
     byte[] fetchStickerAsset(@NonNull final String identifier, @NonNull final String name) throws IOException {

         Log.i("RecyclerView", "class MainActivity, " +
                 "  byte[] fetchStickerAsset(@NonNull final String identifier, @NonNull final String name)  ");

        String current_path = assetsFolderPath + "/" + identifier + "/" + name;

        try (final InputStream is = new FileInputStream(current_path);
             final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int read;
            byte[] data = new byte[16384];
            while ((read = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
            }
            return buffer.toByteArray();
        }
    }
    //--------------------------------------------------------------------------------------------------
    @NonNull
    private List<Sticker> getStickersForPack(Context context, StickerPack stickerPack) {

        Log.i("RecyclerView", "class MainActivity, " +
                "  private List<Sticker> getStickersForPack(Context context, StickerPack stickerPack) ");

        final List<Sticker> stickers = fetchFromContentProviderForStickers(stickerPack);
        for (Sticker sticker : stickers) {
            try {
                bytes = fetchStickerAsset(stickerPack.identifier, sticker.imageFileName);
                if (bytes.length <= 0) {
                    throw new IllegalStateException("Asset file is empty, pack: " + stickerPack.name + ", sticker: " + sticker.imageFileName);
                }
                sticker.setSize(bytes.length);
            } catch (IOException | IllegalArgumentException e) {
                throw new IllegalStateException("Asset file doesn't exist. pack: " + stickerPack.name + ", sticker: " + sticker.imageFileName, e);
            }
        }
        return stickers;
    }

  //--------------------------------------------------------------------------------------------------
  private void initAnimatedPack(){

      Log.i("RecyclerView", "class MainActivity, " +
              " private void initAnimatedPack() ");

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
  private  void initStaticPack() throws IOException, ParseException {

      Log.i("RecyclerView", "class MainActivity, " +
              "private  void initStaticPack() ");

      String assetsPath = getAbsoluteAssetPath(asset_pack, "");
      if (assetsPath == null) {
          getPackStates(asset_pack);



      }
      if (assetsPath != null) {

          try {
              // parsing file "JSONExample.json"
              Object obj = new JSONParser().parse(new FileReader(assetsFolderPath +"/contents.json"));
              // typecasting obj to JSONObject
              jo = (JSONObject) obj;
              stickerPackList = fetchFromJSON(jo);
          } catch (IOException  | ParseException e1) {
              e1.printStackTrace();
          }
          for (StickerPack stickerPack : stickerPackList) {
              final List<Sticker> stickers = getStickersForPack(getApplicationContext(), stickerPack);
              stickerPack.setStickers(stickers);
            //  StickerPackValidator.verifyStickerPackValidity(stickerPack, bytes);
          }

          final Intent intent = new Intent(this, EntryActivity.class);
          intent.putParcelableArrayListExtra(StickerPackListActivity.EXTRA_STICKER_PACK_LIST_DATA, stickerPackList);
          intent.putExtra("StickerPackList", stickerPackList);
          startActivity(intent);

      }

  }
    //---------------------------------------------------------------------------------------------------
    /**
     * This method will check which button was clicked & call respective method to get file & play
     */
    private void initClickedAssetPack() throws IOException, ParseException {

        Log.i("RecyclerView", "class MainActivity, " +
                "private void initClickedAssetPack()");

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
    private void initAssetPackManager () throws IOException, ParseException {

        Log.i("RecyclerView", "class MainActivity, " +
                "private void initAssetPackManager () ");

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
    private void registerListener() throws IOException, ParseException {

        Log.i("RecyclerView", "class MainActivity, " +
                "private void registerListener()  ");

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

        Log.i("RecyclerView", "class MainActivity, " +
                "private void getPackStates(String assetPackName) ");

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

        Log.i("RecyclerView", "class MainActivity, " +
                " protected void onCreate(Bundle savedInstanceState) ");

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
                    try {
                        initClickedAssetPack();
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
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

        Log.i("Destroy", "class MainActivity, " +
                "protected void onDestroy() ");

        Log.i("RecyclerView", "class MainActivity, " +
                "protected void onDestroy() ");

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

        Log.i("RecyclerView", "class MainActivity, " +
                " private String getAbsoluteAssetPath(String assetPack, String relativeAssetPath) ");

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