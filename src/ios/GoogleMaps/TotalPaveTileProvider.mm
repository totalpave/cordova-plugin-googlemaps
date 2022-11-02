
#import "TotalPaveTileProvider.h"
#import <tp/TileGenerator.h>
#import <tp/Logger.h>
#import <tp/ErrorCode.h>

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

    int status = 0;
    TP::GeneratorSettingsBuilder builder;
    builder.setDBPath([[[NSURL URLWithString:dbPathStr] path] UTF8String])
        .setSQLString([selectQuery UTF8String]);
        
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

- ( UIImage * _Nullable ) tileForX:(NSUInteger)x y:(NSUInteger)y zoom:(NSUInteger)zoom {
    std::vector<uint8_t> buffer;
    int status = TP::TileGenerator::getInstance()->render(buffer, (int)x, (int)y, (int)zoom);
    if (status != 0) {
        NSLog(@"Error during tile render, code: %i", status);
    }
    return [[UIImage alloc] initWithData: [[NSData alloc] initWithBytes:buffer.data() length:buffer.size()]];
}

- (void) reload:(NSError*_Nonnull*_Nonnull) error {
    [self $load:error];
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
