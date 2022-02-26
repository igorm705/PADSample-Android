package com.example.padsample;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
    String assetsFolderPath = "";
    String animatedAssetPack = "animated_asset_pack";
    String staticAssetPack = "static_asset_pack";
    String asset_pack = "";
    ArrayList<StickerPack> stickerPackList = new ArrayList<>();
    JSONObject jo;
    JSONArray stickers;
    Iterator<JSONObject> iterator;
    HashMap<String, JSONObject> identifierSet = new HashMap<String, JSONObject>();

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
      ArrayList<StickerPack> stickerPackList = new ArrayList<>();

      // A JSON array. JSONObject supports java.util.List interface.
      JSONArray sticker_packs = (JSONArray) jo.get("sticker_packs");


      // An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
      // Iterators differ from enumerations in two ways:
      // 1. Iterators allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
      // 2. Method names have been improved.
       iterator = sticker_packs.iterator();
      while (iterator.hasNext()) {
          final String ANIMATED_STICKER_PACK = "animated_sticker_pack";
          final String identifier = (String) jo.get("sticker_pack_identifier");
          final String name = (String) jo.get("sticker_pack_name");
          final String publisher = (String) jo.get("sticker_pack_publisher");
          final String trayImage = (String) jo.get("sticker_pack_icon");
          final String androidPlayStoreLink = (String) jo.get("android_play_store_link");
          final String iosAppLink = (String) jo.get("ios_app_download_link");
          final String publisherEmail = (String) jo.get("sticker_pack_publisher_email");
          final String publisherWebsite = (String) jo.get("sticker_pack_publisher_website");
          final String privacyPolicyWebsite = (String) jo.get( "sticker_pack_privacy_policy_website");
          final String licenseAgreementWebsite = (String) jo.get("sticker_pack_license_agreement_website");
          final String imageDataVersion = (String) jo.get("image_data_version");

          final boolean avoidCache = (Boolean) jo.get ("whatsapp_will_not_cache_stickers");
          final boolean animatedStickerPack =(Boolean) jo.get ("animated_sticker_pack");
          final StickerPack stickerPack = new StickerPack(identifier, name, publisher, trayImage, publisherEmail, publisherWebsite, privacyPolicyWebsite, licenseAgreementWebsite, imageDataVersion, avoidCache, animatedStickerPack);
          stickerPack.setAndroidPlayStoreLink(androidPlayStoreLink);
          stickerPack.setIosAppStoreLink(iosAppLink);


          JSONObject stickers = (JSONObject) jo.get(iterator);

          // Add keys and values
          identifierSet.put(identifier, stickers);
          stickerPackList.add(stickerPack);
      }

      return stickerPackList;
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
    //------------------------------------------------------------------------------------------------
    @NonNull
    private  List<Sticker> fetchFromContentProviderForStickers(JSONObject jo) {
        //Uri uri = getStickerListUri(identifier);

        final String[] projection = {"sticker_file_name", "sticker_emoji"};
      //  final Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        List<Sticker> stickers = new ArrayList<>();

        // A JSON array. JSONObject supports java.util.List interface.
        JSONArray stickersJSON = (JSONArray) jo.get("stickers");

        iterator = stickersJSON.iterator();
        while (iterator.hasNext()) {
            final String name = (String) jo.get("sticker_file_name");
            final String emojisConcatenated =  (String) jo.get("sticker_emoji");

            List<String> emojis = new ArrayList<>(StickerPackValidator.EMOJI_MAX_LIMIT);
            if (!TextUtils.isEmpty(emojisConcatenated)) {
                emojis = Arrays.asList(emojisConcatenated.split(","));
            }
            stickers.add(new Sticker(name, emojis));
        }

        return stickers;
    }
    //--------------------------------------------------------------------------------------------------
    @NonNull
    private List<Sticker> getStickersForPack(Context context, StickerPack stickerPack) {
        final List<Sticker> stickers = fetchFromContentProviderForStickers(identifierSet.get(stickerPack.identifier));
        for (Sticker sticker : stickers) {
            final byte[] bytes;
            try {
                bytes = fetchStickerAsset(stickerPack.identifier, sticker.imageFileName, context.getContentResolver());
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
              StickerPackValidator.verifyStickerPackValidity(context, stickerPack);
          }
     //     return stickerPackList;
       /*   File dir = new File(assetsFolderPath);
          File[] files = dir.listFiles();
          // Fetching all the files
          for (File file : files) {
              if(file.isFile()) {
                  try {
                      System.out.println("");
                  } catch (Exception e) {
                      e.printStackTrace();
                  }

                  BufferedReader inputStream = null;
                  String line;
                  try {
                      inputStream = new BufferedReader(new FileReader(file));
                      while ((line = inputStream.readLine()) != null) {
                          System.out.println(line);
                      }
                  }catch(IOException e) {
                      System.out.println(e);
                  }
                  finally {
                      if (inputStream != null) {
                          inputStream.close();
                      }
                  }
              }
          }*/



          //  File file = new File(assetsFolderPath + "/fast_follow2.txt");
        //  inputStreamFiles(file);
      }

  }
    //---------------------------------------------------------------------------------------------------
    /**
     * This method will check which button was clicked & call respective method to get file & play
     */
    private void initClickedAssetPack() throws IOException, ParseException {
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