package plugin.google.maps;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.File;
import java.net.URI;
import java.lang.IllegalArgumentException;
import java.lang.RuntimeException;

import com.totalpave.tilegen.TileGenerator;
import com.totalpave.tilegen.GeneratorSettings;
import com.totalpave.tilegen.ScaleItem;
import com.totalpave.tilegen.NoTilesToRenderException;
import com.totalpave.tilegen.TileUnavailableException;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class TotalPaveTileProvider implements TileProvider {
    JSONArray scale;
    GeneratorSettings settings;

    private static final int TILE_SIZE_DP = 256;
    private int tileSize;

    public TotalPaveTileProvider(DisplayMetrics displayMetrics, String dbPath, String selectQuery, JSONArray scale) throws IllegalArgumentException {
        super();

        File fdbPath = new File(URI.create(dbPath));

        this.tileSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TILE_SIZE_DP, displayMetrics);
        this.settings = new GeneratorSettings();
        this.settings.setDBPath(fdbPath.getAbsolutePath())
            .setSQLString(selectQuery)
            .setDpiScale(1.0f)
            .setMinStrokeWidth(1)
            // stroke width and dpi scale settings had some problems on android, which makes the liners considerably thinner than ios
            // IRI has this issue. libtilegen has been mostly fixed but strokeWidth default value is not available for android atm. It's inside the native c code.
            // Because of that, applying dimensions to it causes an exception because it's null.
            // .setStrokeWidth((int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.settings.strokeWidth, displayMetrics)))
            // .setDpiScale(displayMetrics.density)
            .setTileSize(this.tileSize)
            .setAntiAlias(2)
            .setZoomModifier(1f)
            .setZoomModifierThreshold(16);


        try {
            for (int i = 0; i < scale.length(); i++) {
                JSONObject scaleObj = scale.getJSONObject(i);

                Double low = null;
                Double high = null;

                if (!scaleObj.isNull("low")) {
                    low = scaleObj.getDouble("low");
                }

                if (!scaleObj.isNull("high")) {
                    high = scaleObj.getDouble("high");
                }

                int strokeColor = scaleObj.getInt("stroke");
                int fillColor = scaleObj.getInt("fill");

                settings.addScaleItem(new ScaleItem(low, high, strokeColor, fillColor));
            }
        }
        catch (JSONException ex) {
            throw new IllegalArgumentException("Could not parse Scale array.", ex);
        }
        
        this.scale = scale;
        this.$load();
    }

    private void $load() {
        int status = TileGenerator.load(this.settings);
        if (status == 0) {} // No error occurred.
        else if (status == TileGenerator.DATASET_LOAD_ERROR) {
            throw new IllegalArgumentException("Could not load dataset. Logcat may contain addition error messages.");
        }
        else if (status == TileGenerator.INVALID_FEATURE) {
            throw new IllegalArgumentException("Dataset contained invalid features. Logcat may contain addition error messages.");
        }
        else if (status == TileGenerator.UNSUPPORTED_GEOMETRY) {
            throw new IllegalArgumentException("Dataset contained unsupported features. Only LineString and Polygons are supported. Logcat may contain addition error messages.");
        }
        else {
            throw new RuntimeException("Unexpected error received from libtilegen. Error Code: " + status);
        }
    }

    public void reload() {
        this.$load();
        // Tile cache is cleared in PluginTotalPaveTileLayer.
    }

    public void reset() {
        TileGenerator.reset();
        // Tile cache is cleared in PluginTotalPaveTileLayer.
    }

    @Nullable
    @Override
    public Tile getTile(int x, int y, int z) {
        try {
            byte[] data = TileGenerator.render(x, y, z);
            return new Tile(this.tileSize, this.tileSize, data);
        }
        catch (NoTilesToRenderException | TileUnavailableException ex) {
            return TileProvider.NO_TILE;
        }
    }

    public int[] querySourceData(double minLon, double maxLon, double minLat, double maxLat) {
        return TileGenerator.queryGeometryData(minLon, maxLon, minLat, maxLat);
    }
}
