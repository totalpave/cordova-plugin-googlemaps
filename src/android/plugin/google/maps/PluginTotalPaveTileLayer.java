package plugin.google.maps;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

public class PluginTotalPaveTileLayer extends MyPlugin implements MyPluginInterface  {
    protected final String PROVIDER_SUFFIX = "_Provider";
    
    public void reload(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(() -> {
            try {
                ((TotalPaveTileProvider)this.pluginMap.objects.get(args.getString(0) + PROVIDER_SUFFIX)).reload();
                this.$clearTileCache(args.getString(0), callbackContext);
            }
            catch (JSONException e) {
                e.printStackTrace();
                callbackContext.error("" + e.getMessage());
                return;
            }
        });
    }

    private void $clearTileCache(String id, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(() -> {
            ((TileOverlay) this.pluginMap.objects.get(id)).clearTileCache();
            callbackContext.success();
        });
    }

    public void create(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        JSONObject opts = args.getJSONObject(1);
        final String hashCode = args.getString(2);
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
            return;
        }

        cordova.getThreadPool().execute(() -> {
            TotalPaveTileProvider provider;
            try {
                provider = new TotalPaveTileProvider(
                    cordova.getContext().getResources().getDisplayMetrics(),
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

                String id = "totalpavetilelayer_" + hashCode;
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
        });
    }

    public void remove(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        final PluginTotalPaveTileLayer tileLayer = this.getTotalPaveTileLayer(id);
        if (tileLayer == null) {
            callbackContext.success();
            return;
        }
        ((TotalPaveTileProvider)this.pluginMap.objects.get(id + PROVIDER_SUFFIX)).reset();
        this.pluginMap.objects.remove(id);
        this.pluginMap.objects.remove(id + PROVIDER_SUFFIX);

        this.$clearTileCache(args.getString(0), callbackContext);
    }
}
