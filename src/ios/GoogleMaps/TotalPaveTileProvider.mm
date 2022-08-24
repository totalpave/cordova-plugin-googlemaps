
#import "TotalPaveTileProvider.h"
#import <libtilegen/tp/TileGenerator.h>
#import <libtilegen/tp/Logger.h>
#import <libtilegen/tp/ErrorCode.h>
#import <libtilegen/tp/Scale.h>
#import <libtilegen/tp/GeneratorSettings.h>

@implementation TotalPaveTileProvider {
    NSArray* scale;
    TP::Logger* logger;
    TP::GeneratorSettings settings;
}

NSString* const LIB_TILE_GEN_DOMAIN = @"TotalPaveTileProviderLibTileGen";

- (id)initWithDB:(NSString *)dbPathStr selectQuery:(NSString *)selectQuery scale:(NSArray*)scale error:(NSError*_Nonnull*_Nonnull) error {
    self = [super init];
    self->scale = scale;
    self->logger = new TP::Logger("TotalPaveTileProvider");
    TP::Logger::setActiveLogger(self->logger);

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
    
    TP::GeneratorSettingsBuilder builder;
    builder.setDBPath([[[NSURL URLWithString:dbPathStr] path] UTF8String])
        .setSQLString([selectQuery UTF8String])
        .setDpiScale(dpiScale)
        .setAntiAlias(1)
        .setTileSize(512)
        .setZoomModifier(0.3)
        .setZoomModifierThreshold(16);
        
    for (NSUInteger i = 0, length = scale.count; i < length; ++i) {
        NSDictionary *item = scale[i];
        TP::Scale::Item scaleItem;
        scaleItem.low = [(NSNumber*)[item valueForKey:@"low"] doubleValue];
        NSNumber* high = [item valueForKey:@"high"];
        if (![high isEqual:[NSNull null]]) {
            scaleItem.high = [high doubleValue];
        }
        // Minor Concern, we are assigning uint to uint32_t.
        scaleItem.strokeColor = [(NSNumber*)[item valueForKey:@"stroke"] unsignedIntValue];
        scaleItem.fillColor = [(NSNumber*)[item valueForKey:@"fill"] unsignedIntValue];
        builder.addScaleItem(scaleItem);
    }
    
    self->settings = builder.build();
    [self $load:error];
    return self;
}

 - (void) reset {
    TP::TileGenerator::getInstance()->reset();
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        [self clearTileCache];
    }];
 }

- ( UIImage * _Nullable ) tileForX:(NSUInteger)x y:(NSUInteger)y zoom:(NSUInteger)zoom {
    std::vector<uint8_t> buffer;

    int status = TP::TileGenerator::getInstance()->render(buffer, (int)x, (int)y, (int)zoom);
    if (status == TP::ErrorCode::TILE_UNAVAILABLE) {
        return nil;
    }
    if (status == TP::ErrorCode::NO_FEATURES_TO_RENDER) {
        return kGMSTileLayerNoTile;
    }
    else if (status != 0) {
        NSLog(@"Error during tile render, code: %i", status);
    }
    return [[UIImage alloc] initWithData: [[NSData alloc] initWithBytes:buffer.data() length:buffer.size()]];
}

- (void) reload:(NSError*_Nonnull*_Nonnull) error {
    [self $load:error];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        [self clearTileCache];
    }];
}

- (void) $load:(NSError*_Nonnull*_Nonnull) error {
    int status = 0;
    TP::TileGenerator::getInstance()->load(status, self->settings);
    if (status == 0) {} // No error occurred.
    else if (status == TP::ErrorCode::DATASET_LOAD_ERROR) {
        *error = [[NSError alloc]
            initWithDomain:LIB_TILE_GEN_DOMAIN
            code:status
            userInfo: @{
                NSLocalizedDescriptionKey: @"Could not load dataset."
            }
        ];
        return;
    }
    else if (status == TP::ErrorCode::INVALID_FEATURE) {
        *error = [[NSError alloc]
            initWithDomain:LIB_TILE_GEN_DOMAIN
            code:status
            userInfo: @{
                NSLocalizedDescriptionKey: @"Dataset contained invalid features."
            }
        ];
        return;
    }
    else if (status == TP::ErrorCode::UNSUPPORTED_GEOMETRY) {
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
