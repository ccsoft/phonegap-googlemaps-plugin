package plugin.google.maps;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class PluginGroundOverlay extends MyPlugin {
  private BitmapDescriptor dummyImg;
  
  @SuppressLint("UseSparseArrays")
  @Override
  public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
    super.initialize(cordova, webView);
    Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    this.dummyImg = BitmapDescriptorFactory.fromBitmap(bitmap);
  }

  /**
   * Create ground overlay
   * 
   * @param args
   * @param callbackContext
   * @throws JSONException
   */
  @SuppressWarnings("unused")
  private void createGroundOverlay(final JSONArray args, final CallbackContext callbackContext) throws JSONException {

    JSONObject opts = args.getJSONObject(1);

    GroundOverlayOptions options = new GroundOverlayOptions();
    
    if (opts.has("anchor")) {
      JSONArray anchor = opts.getJSONArray("anchor");
      options.anchor((float)anchor.getDouble(0), (float)anchor.getDouble(1));
    }
    if (opts.has("bearing")) {
      options.bearing((float)opts.getDouble("bearing"));
    }
    if (opts.has("opacity")) {
      options.transparency((float)opts.getDouble("opacity"));
    }
    if (opts.has("zIndex")) {
      options.zIndex((float)opts.getDouble("zIndex"));
    }
    if (opts.has("visible")) {
      options.visible(opts.getBoolean("visible"));
    }
    
    if (opts.has("position") == true && opts.has("bounds") == false) {
      float width = 0;
      float height = 0;
      if (opts.has("width")) {
        width = (float)opts.getDouble("width");
      }
      if (opts.has("height")) {
        height = (float)opts.getDouble("height");
      }
      JSONArray position = opts.getJSONArray("position");
      LatLng latLng = new LatLng(position.getDouble(0), position.getDouble(1));
      if (height == 0) {
        options.position(latLng, width);
      } else {
        options.position(latLng, width, height);
      }
    }
    if (opts.has("position") == false && opts.has("bounds") == true) {
      JSONArray points = opts.getJSONArray("bounds");
      LatLngBounds bounds = PluginUtil.JSONArray2LatLngBounds(points);
      options.positionFromBounds(bounds);
    }

    // Load a dummy image
    options.image(this.dummyImg);
    
    GroundOverlay groundOverlay = this.map.addGroundOverlay(options);

    // Load image
    String url = opts.getString("url");
    if (url != null && url.length() > 0) {
      if (url.indexOf("http") == 0) {
        AsyncLoadImage task = new AsyncLoadImage(groundOverlay, "setImage");
        task.execute(url);
      } else {
        groundOverlay.setImage(BitmapDescriptorFactory.fromAsset(url));
      }
    }
    
    String id = "ground_" + groundOverlay.getId();
    this.objects.put(id, groundOverlay);
    
    callbackContext.success(id);
  }

}