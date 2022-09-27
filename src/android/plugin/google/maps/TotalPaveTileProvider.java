package plugin.google.maps;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.File;
import java.lang.IllegalArgumentException;
import java.lang.RuntimeException;

import com.totalpave.libtilegen.TileGenerator;
import com.totalpave.libtilegen.GeneratorSettings;
import com.totalpave.libtilegen.ScaleItem;

public class TotalPaveTileProvider implements TileProvider {
    Context context;
    JSONArray scale;

    public TotalPaveTileProvider(Context applicationContext, String dbName, String selectQuery, JSONArray scale) throws IllegalArgumentException {
        super();

        File file = applicationContext.getDatabasePath(dbName);

        GeneratorSettings settings = new GeneratorSettings();
        settings.dbPath = file.getAbsolutePath();
        settings.sql = selectQuery;

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
        
        int status = TileGenerator.load(settings);
        this.scale = scale;

        // TO-DO Give scale data to TileGenerator

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

    @Nullable
    @Override
    public Tile getTile(int x, int y, int z) {
        byte[] data = TileGenerator.render(x, y, z);
        return new Tile(256, 256, data);
    }
}
