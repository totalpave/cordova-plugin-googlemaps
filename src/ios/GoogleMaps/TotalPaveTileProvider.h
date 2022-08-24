#import <GoogleMaps/GoogleMaps.h>

@interface TotalPaveTileProvider : GMSSyncTileLayer

- (id)initWithDB:(NSString *)dbName selectQuery:(NSString *)selectQuery scale:(NSArray*)scale;

@end
