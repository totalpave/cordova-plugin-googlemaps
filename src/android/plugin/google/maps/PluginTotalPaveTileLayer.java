package plugin.google.maps;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

public class PluginTotalPaveTileLayer extends MyPlugin implements MyPluginInterface  {
    protected final String PROVIDER_SUFFIX = "_Provider";
    
    public void create(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        JSONObject opts = args.getJSONObject(1);
        final String hashCode = args.getString(2);
        TotalPaveTileProvider provider;
        // See PluginPolyline for examples on how to use opts

        if (!opts.has("dbPath")) {
            callbackContext.error("DB Path is required.");
            return;
        }

        if (!opts.has("selectQuery")) {
            callbackContext.error("Select Query is required.");
            return;
        }

        if (!opts.has("scale")) {
            callbackContext.error("scale is required.");
        }

        try {
            provider = new TotalPaveTileProvider(
                this.cordova.getActivity().getApplicationContext(),
                opts.getString("dbPath"),
                opts.getString("selectQuery"),
                opts.getJSONArray("scale")
            );
        }
        catch (Exception ex) {
            callbackContext.error(ex.getMessage());
            return;
        }

        cordova.getActivity().runOnUiThread(() -> {
            TileOverlay overlay = map.addTileOverlay(new TileOverlayOptions()
                .tileProvider(provider)
                .fadeIn(false)
            );

            String id = "totalPaveTileLayer_" + hashCode;
            this.pluginMap.objects.put(id, overlay);
            this.pluginMap.objects.put(id + PROVIDER_SUFFIX, provider);

            try {
                JSONObject result = new JSONObject();
                result.put("hashCode", hashCode);
                result.put("__pgmId", id);
                callbackContext.success(result);
            } catch (JSONException e) {
                e.printStackTrace();
                callbackContext.error("" + e.getMessage());
            }
        });
    }

    public void remove(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        final PluginTotalPaveTileLayer tileLayer = this.getTotalPaveTileLayer(id);
        if (tileLayer == null) {
            callbackContext.success();
            return;
        }
        this.pluginMap.objects.remove(id);
        this.pluginMap.objects.remove(id + PROVIDER_SUFFIX);

        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                callbackContext.success();
            }
        });
    }
}
