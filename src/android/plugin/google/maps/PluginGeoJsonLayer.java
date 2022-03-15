package plugin.google.maps;

// import plugin.google.maps.geojson.GeometryFactory;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLngBounds;

import com.google.maps.android.data.geojson.GeoJsonLayer;
// import com.google.maps.android.data.Geometry;
// import com.google.maps.android.data.geojson.GeoJsonFeature;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PluginGeoJsonLayer extends MyPlugin implements MyPluginInterface  {
    @Override
    public void initialize(final CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    /**
    * Create GeoJSON layer
    *
    * @param args
    * @param callbackContext
    * @throws JSONException
    */
    public void create(JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject geometry = args.getJSONObject(1);
        String hashCode = args.getString(2);
        GeoJsonLayer layer = new GeoJsonLayer(this.map, geometry);
        this.pluginMap.objects.put("geojsonlayer_" + hashCode, layer);
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layer.addLayerToMap();
            }
        });
    }


    @Override
    protected void clear() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] keys = pluginMap.objects.keys.toArray(new String[pluginMap.objects.size()]);
                Object object;
                for (String key : keys) {
                    if (key.startsWith("geojsonlayer_")) {
                        GeoJsonLayer layer = (GeoJsonLayer) pluginMap.objects.remove(key);
                        layer.removeLayerFromMap();
                        layer = null;
                    }
                }
            }
        });
    }

    /**
    * Remove this tile layer
    * @param args
    * @param callbackContext
    * @throws JSONException
    */
    public void remove(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final String id = args.getString(0);
        final GeoJsonLayer geoJsonLayer = (GeoJsonLayer)pluginMap.objects.get(id);
        if (geoJsonLayer == null) {
            callbackContext.success();
            return;
        }
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                geoJsonLayer.removeLayerFromMap();
                callbackContext.success();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.clear();
    }

    // private Geometry parseGeometry(final JSONArray geometries) {
    //     for (int i = 0, length = geometries.length(); i < length; ++i) {
    //         JSONObject geometry = geometries.getJSONObject(i);
            
    //     }
    // }

    // /**
    // * Set image of the ground-overlay
    // * @param args
    // * @param callbackContext
    // * @throws JSONException
    // */
    // public void addFeature(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    //     String id = args.getString(0);
    //     JSONObject data = args.getJSONObject(1);
    //     Geometry geo = GeometryFactory.create(data.getInt('type'), data.getJSONObject('geometry'));
    //     GeoJsonLayer layer = (GeoJsonLayer) pluginMap.objects.get(id);

    //     Object untypedFeatureID = args.get(2);
    //     String featureID;
    //     if (untypedFeatureID instanceof Integer) {
    //         featureID = ((Integer) untypedFeatureID).toString();
    //     }
    //     else {
    //         featureID = (String) untypedFeatureID;
    //     }

    //     LatLngBounds.Builder builder = LatLngBounds.builder();



    //     GeoJsonFeature feature = new GeoJsonFeature(
    //         geo,
    //         featureID,
    //         new HashMap<String,String>(),

    //     )
    //     layer.addFeature()
    //     public GeoJsonFeature(
    //         Geometry geometry,
    //         java.lang.String id,
    //         java.util.HashMap<java.lang.String,java.lang.String> properties,
    //         LatLngBounds boundingBox
    //     )
    // }

    // /**
    // * Set image of the ground-overlay
    // * @param args
    // * @param callbackContext
    // * @throws JSONException
    // */
    // public void addFeatures(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        
    // }

}
