package plugin.google.maps.geojson;

// These enums map to the GeoJson geometries.
// See classes that implement http://googlemaps.github.io/android-maps-utils/javadoc/com/google/maps/android/data/Geometry.html
public enum GeometryType {
    /**
        JSONObject<{
            double latitude,
            double longitude,
            double altitude (optional)
        }>
     */
    POINT           (1),
    /**
        JSONObject<{
            points: JSONArray<GeometryType.POINT data structure>
        }>
        
        Additional Notes: While the typing is the exact same as LINE, note use MULTI_POINT returns a GeoJsonMultiPoint while LINE returns a GeoJsonLineString.
     */
    MULTI_POINT     (2),
    /**
        JSONObject<{
            points: JSONArray<GeometryType.POINT data structure>
        }>

        Additional Notes: While the typing is the exact same as MULTI_POINT, note use LINE returns a GeoJsonLineString while MULTI_POINT returns a GeoJsonMultiPoint.
     */
    LINE            (3),
    /**
        JSONObject<{
            lines: JSONArray<GeometryType.LINE data structure>
        }>
     */
    MULTI_LINE      (4),
    /**
        JSONObject<{
            points: JSONArray<{
                double latitude,
                double longitude
            }>
        }>

        Additional Notes: Note that points in here do not support altitude.
     */
    POLYGON         (5),
    /**
        JSONObject<{
            polygons: JSONArray<GeometryType.POLYGON data structure>
        }>
     */
    MULTI_POLYGON   (6)
}