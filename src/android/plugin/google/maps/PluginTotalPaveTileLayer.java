package plugin.google.maps;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.totalpave.libtilegen.TileGenerator;

public class PluginTotalPaveTileLayer extends MyPlugin implements MyPluginInterface  {
    protected final String PROVIDER_SUFFIX = "_Provider";

    public void reload(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(() -> {
            try {
                String key = args.getString(0);
                String providerKey = key + PROVIDER_SUFFIX;
                // The objects should exist 99% of the time but we'd had odd crash reports that indicate it didn't exist. So let's at least prevent the crashes.
                // We do still want errors that propagate to JS land to keep track of this issue.
                if (this.pluginMap.objects.containsKey(providerKey)) {
                    ((TotalPaveTileProvider)this.pluginMap.objects.get(providerKey)).reload();
                }
                else {
                    throw new JSONException("PluginTotalPaveTileLayer.reload could not find provider in pluginMap for key: " + key);
                }
                if (this.pluginMap.objects.containsKey(key)) {
                    this.$clearTileCache((TileOverlay)this.pluginMap.objects.get(key), callbackContext);
                }
                else {
                    throw new JSONException("PluginTotalPaveTileLayer.reload could not find overlay in pluginMap for key: " + key);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                callbackContext.error("" + e.getMessage());
                return;
            }
        });
    }

    private void $clearTileCache(TileOverlay overlay, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(() -> {
            overlay.clearTileCache();
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

    public void setVisible(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        Boolean isVisible = args.getBoolean(1);
        if (this.pluginMap.objects.containsKey((id))) {
            TileOverlay overlay = (TileOverlay) this.pluginMap.objects.get(id);
            overlay.setVisible(isVisible);
            callbackContext.success();
        }
        else {
            callbackContext.error("PluginTotalPaveTileLayer.setVisible could not find overlay in pluginMap for key: " + id);
        }
    }

    public void isVisible(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        if (this.pluginMap.objects.containsKey((id))) {
            TileOverlay overlay = (TileOverlay) this.pluginMap.objects.get(id);
            callbackContext.success(overlay.isVisible() ? 1 : 0);
        }
        else {
            callbackContext.error("PluginTotalPaveTileLayer.setVisible could not find overlay in pluginMap for key: " + id);
        }
    }

    public void remove(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        final PluginTotalPaveTileLayer tileLayer = this.getTotalPaveTileLayer(id);
        if (tileLayer == null) {
            callbackContext.success();
            return;
        }
        if (this.pluginMap.objects.containsKey(id + PROVIDER_SUFFIX)) {
            ((TotalPaveTileProvider)this.pluginMap.objects.get(id + PROVIDER_SUFFIX)).reset();
        }
        else {
            callbackContext.error("PluginTotalPaveTileLayer.remove could not find overlay in pluginMap for key: " + id);
            return;
        }
        if (this.pluginMap.objects.containsKey((id))) {
            TileOverlay overlay = (TileOverlay)this.pluginMap.objects.get(id);
            this.$clearTileCache(overlay, callbackContext);
        }
        else {
            callbackContext.error("PluginTotalPaveTileLayer.remove could not find overlay in pluginMap for key: " + id);
            return;
        }
        this.pluginMap.objects.remove(id);
        this.pluginMap.objects.remove(id + PROVIDER_SUFFIX);
    }

    public void querySourceData(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(() -> {
            try {
                String key = args.getString(0);
                String providerKey = key + PROVIDER_SUFFIX;
                // The objects should exist 99% of the time but we'd had odd crash reports that indicate it didn't exist. So let's at least prevent the crashes.
                // We do still want errors that propagate to JS land to keep track of this issue.
                if (this.pluginMap.objects.containsKey(providerKey)) {
                    int[] ids = ((TotalPaveTileProvider)this.pluginMap.objects.get(providerKey)).querySourceData(args.getDouble(1), args.getDouble(2), args.getDouble(3), args.getDouble(4));
                    JSONArray data = new JSONArray(ids);
                    callbackContext.success(data);
                }
                else {
                    throw new JSONException("PluginTotalPaveTileLayer.reload could not find provider in pluginMap for key: " + key);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                callbackContext.error("" + e.getMessage());
                return;
            }
        });
    }
}
