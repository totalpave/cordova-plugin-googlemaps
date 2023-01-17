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

import com.totalpave.libtilegen.TileGenerator;
import com.totalpave.libtilegen.GeneratorSettings;
import com.totalpave.libtilegen.ScaleItem;
import com.totalpave.libtilegen.NoTilesToRenderException;
import com.totalpave.libtilegen.TileUnavailableException;

public class TotalPaveTileProvider implements TileProvider {
    JSONArray scale;
    GeneratorSettings settings;

    public TotalPaveTileProvider(String dbPath, String selectQuery, JSONArray scale) throws IllegalArgumentException {
        super();

        File fdbPath = new File(URI.create(dbPath));

        this.settings = new GeneratorSettings();
        this.settings.setDBPath(fdbPath.getAbsolutePath())
            .setSQLString(selectQuery)
            .setDpiScale(1)
            .setMinStrokeWidth(1)
            .setTileSize(512)
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
            return new Tile(512, 512, data);
        }
        catch (NoTilesToRenderException | TileUnavailableException ex) {
            return TileProvider.NO_TILE;
        }
    }
}
