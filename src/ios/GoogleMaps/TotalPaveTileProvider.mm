
#import "TotalPaveTileProvider.h"
#import <tilegen/tilegen.h>

@implementation TotalPaveTileProvider {
    NSArray* $scale;
    TPITilegenLogger* $logger;
    TPITilegenGeneratorSettings* $settings;
}

NSString* const LIB_TILE_GEN_DOMAIN = @"TotalPaveTileProviderLibTileGen";

- (id)initWithDB:(NSString *)dbPathStr selectQuery:(NSString *)selectQuery scale:(NSArray*)scale error:(NSError*_Nonnull*_Nonnull) error {
    self = [super init];
    
    $scale = scale;
    
    $logger = [[TPITilegenLogger alloc] init:@"TotalPaveTileProvider"];
    [TPITilegenLogger setActiveLogger: $logger];

    /**
        https://developer.apple.com/documentation/uikit/uiscreen/1617836-scale?language=objc
        The default logical coordinate space is measured using points. For Retina displays,
        the scale factor may be 3.0 or 2.0 and one point can represented by nine or four pixels,
        respectively. For standard-resolution displays, the scale factor is 1.0 and one point equals one pixel.

        Note, standard resolution displays are no longer made. Even the iPhone 6 Simulator have a UIScreen scale of 2.
        In otherwords, there seems to be a lack of devices and simulators to test standard resolutions (UIScreen scale of 1).
    */
    int dpiScale = 1;
    if ([UIScreen mainScreen].scale == 2) {
        dpiScale = 4;
    }
    else if ([UIScreen mainScreen].scale == 3) {
        dpiScale = 9;
    }
    
    TPITilegenGeneratorSettingsBuilder* builder = [[TPITilegenGeneratorSettingsBuilder alloc] init];
    [builder setDBPath: [[NSURL URLWithString:dbPathStr] path]];
    [builder setSQLString: selectQuery];
    [builder setDPIScale: dpiScale];
    [builder setAntiAlias: 1];
    [builder setTileSize: 512];
    [builder setZoomModifier: 0.3f];
    [builder setZoomModifierThreshold:16];
        
    for (NSUInteger i = 0, length = scale.count; i < length; ++i) {
        NSDictionary* item = scale[i];
        
        NSNumber* ohigh = [item valueForKey:@"high"];
        double high = std::numeric_limits<double>::infinity();
        if (![ohigh isEqual:[NSNull null]]) {
            high = [ohigh doubleValue];
        }
        
        TPITilegenScaleItem* scaleItem = [
            [TPITilegenScaleItem alloc]
            initLow: [(NSNumber*)[item valueForKey:@"low"] doubleValue]
            high: high
            stroke: [(NSNumber*)[item valueForKey:@"stroke"] unsignedIntValue]
            fill: [(NSNumber*)[item valueForKey:@"fill"] unsignedIntValue]
        ];
        
        [builder addScaleItem: scaleItem];
    }
    
    $settings = [builder build];
    [self $load:error];
    return self;
}

 - (void) reset {
     [TPITilegenTileGenerator reset];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        [self clearTileCache];
    }];
 }

- ( UIImage * _Nullable ) tileForX:(NSUInteger)x y:(NSUInteger)y zoom:(NSUInteger)zoom {
    NSMutableData* buffer = [[NSMutableData alloc] init];
    
    int status = [TPITilegenTileGenerator render:buffer x:(unsigned int)x y:(unsigned int)y z:(unsigned int)zoom];

    if (status == TPITilegen_TILE_UNAVAILABLE) {
        return nil;
    }
    if (status == TPITilegen_NO_FEATURES_TO_RENDER) {
        return kGMSTileLayerNoTile;
    }
    else if (status != 0) {
        NSLog(@"Error during tile render, code: %i", status);
    }
    return [[UIImage alloc] initWithData: buffer];
}

- (void) reload:(NSError*_Nonnull*_Nonnull) error {
    [self $load:error];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        [self clearTileCache];
    }];
}

- (void) $load:(NSError*_Nonnull*_Nonnull) error {
    int status = 0;
    [TPITilegenTileGenerator load:status withSettings: $settings];
    
    if (status == 0) {} // No error occurred.
    else if (status == TPITilegen_DATASET_LOAD_ERROR) {
        *error = [[NSError alloc]
            initWithDomain:LIB_TILE_GEN_DOMAIN
            code:status
            userInfo: @{
                NSLocalizedDescriptionKey: @"Could not load dataset."
            }
        ];
        return;
    }
    else if (status == TPITilegen_INVALID_FEATURE) {
        *error = [[NSError alloc]
            initWithDomain:LIB_TILE_GEN_DOMAIN
            code:status
            userInfo: @{
                NSLocalizedDescriptionKey: @"Dataset contained invalid features."
            }
        ];
        return;
    }
    else if (status == TPITilegen_UNSUPPORTED_GEOMETRY) {
        *error = [[NSError alloc]
            initWithDomain:LIB_TILE_GEN_DOMAIN
            code:status
            userInfo: @{
                NSLocalizedDescriptionKey: @"Dataset contained unsupported features. Only LineString and Polygons are supported."
            }
        ];
        return;
    }
    else {
        *error = [[NSError alloc]
            initWithDomain:LIB_TILE_GEN_DOMAIN
            code:status
            userInfo: @{
                NSLocalizedDescriptionKey: @"Unexpected error received from libtilegen."
            }
        ];
        return;
    }
}

@end
