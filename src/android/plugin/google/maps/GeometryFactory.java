package plugin.google.maps.geojson;

import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonPoint;
import com.google.maps.android.data.geojson.GeoJsonMultiPoint;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonMultiLineString;
import com.google.maps.android.data.geojson.GeoJsonPolygon;
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon;

import com.google.android.gms.maps.model.LatLng;

import java.lang.UnsupportedOperationException;

import org.json.JSONException;


public class GeometryFactory {
    public static Geometry create(GeometryType type, JSONObject geometry) throws UnsupportedOperationException, JSONException {
        switch (type) {
            case GeometryType.POINT:
                LatLng latLng = new LatLng(geometry.getDouble('latitude'), geometry.getDouble('longitude'));
                if (geometry.has('altitude')) {
                    return new GeoJsonPoint(latLng, geometry.getDouble('altitude'));
                }
                else {
                    return new GeoJsonPoint(latLng);
                }
            case GeometryType.MULTI_POINT:
                JSONArray points = geometry.getJSONArray('points');
                List<GeoJsonPoint> geopoints = new List(points.length());
                for (int i = 0, length = points.length(); i < length; ++i) {
                    geopoints.add(i, GeometryFactory.create(GeometryType.POINT, point.getJSONObject(i)));
                }
                return new GeoJsonMultiPoint(geopoints);
            case GeometryType.LINE:
                JSONArray points = geometry.getJSONArray('points');
                List<LatLng> geopoints = new List(points.length());
                List<Double> altitudes = new List(points.length());
                boolean hadAnAltitude = false;
                for (int i = 0, length = points.length(); i < length; ++i) {
                    JSONObject point = points.getJSONObject(i);
                    LatLng latLng = new LatLng(point.getDouble('latitude'), point.getDouble('longitude'));
                    geopoints.add(i, latLng);
                    if (point.has('altitude')) {
                        hadAnAltitude = true;
                        altitudes.add(i, point.getDouble('altitude'));
                    }
                }

                if (hadAnAltitude) {
                    return new GeoJsonLineString(geopoints, altitudes);
                }
                else {
                    return new GeoJsonLineString(geopoints);
                }
            case GeometryType.MULTI_LINE:
                JSONArray lines = geometry.getJSONArray('lines');
                List<GeoJsonLineString> geolines = new List(lines.length());
                for (int i = 0, length = lines.length(); i < length; ++i) {
                    geolines.add(i, GeometryFactory.create(GeometryType.LINE, point.getJSONObject(i)));
                }
                return new GeoJsonMultiLineString(geolines);
            case GeometryType.POLYGON:
                JSONArray points = geometry.getJSONArray('points');
                List<LatLng> geopoints = new List(points.length());
                for (int i = 0, length = points.length(); i < length; ++i) {
                    JSONObject point = points.getJSONObject(i);
                    LatLng latLng = new LatLng(point.getDouble('latitude'), point.getDouble('longitude'));
                    geopoints.add(i, latLng);
                }
                return new GeoJsonPolygon(geopoints);
            case GeometryType.MULTI_POLYGON:
                JSONArray polygons = geometry.getJSONArray('polygons');
                List<GeoJsonLineString> geopolygons = new List(polygons.length());
                for (int i = 0, length = polygons.length(); i < length; ++i) {
                    geopolygons.add(i, GeometryFactory.create(GeometryType.POLYGON, polygons.getJSONObject(i)));
                }
                return new GeoJsonMultiPolygon(geopolygons);
            default:
                throw new UnsupportedOperationException('Unsupported GeometryType: ' + type.toString());
       }
    }
}