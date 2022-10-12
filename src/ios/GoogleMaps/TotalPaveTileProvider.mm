
#import "TotalPaveTileProvider.h"
#import <tp/TileGenerator.h>
#import <tp/Logger.h>

@implementation TotalPaveTileProvider {
    NSArray* scale;
    TP::Logger* logger;
}


- (id)initWithDB:(NSString *)dbPathStr selectQuery:(NSString *)selectQuery scale:(NSArray*)scale {
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
    
    TP::TileGenerator::getInstance()->load(status, builder.build());
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

@end
