// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "cordova-plugin-googlemaps",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "cordova-plugin-googlemaps",
            type: .static,
            targets: ["cordova-plugin-googlemaps"])
    ],
    dependencies: [
        .package(url: "https://github.com/apache/cordova-ios.git", from: "8.0.0"),
        .package(url: "https://github.com/googlemaps/ios-maps-sdk", from: "10.0.0"),
        .package(url: "https://github.com/totalpaveinc/libtilegen", exact: "0.5.13")
    ],
    targets: [
        .target(
            name: "cordova-plugin-googlemaps",
            dependencies: [
                .product(name: "Cordova", package: "cordova-ios"),
                .product(name: "GoogleMaps", package: "ios-maps-sdk"),
                .product(name: "@totalpave/cordova-plugin-tilegen", package: "libtilegen")
            ],
            path: "src/ios",
            sources: [
                "GoogleMaps/PluginObjects.m",
                "GoogleMaps/PluginCircle.m",
                "GoogleMaps/PluginGeocoder.m",
                "GoogleMaps/PluginLocationService.m",
                "GoogleMaps/PluginEnvironment.m",
                "GoogleMaps/CordovaGoogleMaps.m",
                "GoogleMaps/PluginMapViewController.m",
                "GoogleMaps/PluginGroundOverlay.m",
                "GoogleMaps/PluginMap.m",
                "GoogleMaps/PluginMarker.m",
                "GoogleMaps/PluginUtil.m",
                "GoogleMaps/PluginPolygon.m",
                "GoogleMaps/PluginPolyline.m",
                "GoogleMaps/PluginTileProvider.m",
                "GoogleMaps/TBXML.m",
                "GoogleMaps/PluginTileOverlay.m",
                "GoogleMaps/MFGoogleMapAdditions/GMSCoordinateBounds+Geometry.m",
                "GoogleMaps/MyPluginLayer.m",
                "GoogleMaps/MyPluginScrollView.m",
                "GoogleMaps/PluginMarkerCluster.m",
                "GoogleMaps/UIImageCache.m",
                "GoogleMaps/PluginStreetViewPanorama.m",
                "GoogleMaps/PluginStreetViewPanoramaController.m",
                "GoogleMaps/PluginViewController.m",
                "GoogleMaps/PluginTotalPaveTileLayer.m",
                "GoogleMaps/TotalPaveTileProvider.mm"
            ],
            resources: [
                .copy("strings/pgm_Localizable_ar.json"),
                .copy("strings/pgm_Localizable_da.json"),
                .copy("strings/pgm_Localizable_de.json"),
                .copy("strings/pgm_Localizable_en.json"),
                .copy("strings/pgm_Localizable_es.json"),
                .copy("strings/pgm_Localizable_fr.json"),
                .copy("strings/pgm_Localizable_hi.json"),
                .copy("strings/pgm_Localizable_in.json"),
                .copy("strings/pgm_Localizable_ja.json"),
                .copy("strings/pgm_Localizable_nb.json"),
                .copy("strings/pgm_Localizable_nl.json"),
                .copy("strings/pgm_Localizable_pl.json"),
                .copy("strings/pgm_Localizable_pt-BR.json"),
                .copy("strings/pgm_Localizable_ru.json"),
                .copy("strings/pgm_Localizable_uk.json"),
                .copy("strings/pgm_Localizable_vi.json")
            ],
            publicHeadersPath: ".",
            cSettings: [
                .headerSearchPath("GoogleMaps/MFGoogleMapAdditions")
            ])
    ]
)