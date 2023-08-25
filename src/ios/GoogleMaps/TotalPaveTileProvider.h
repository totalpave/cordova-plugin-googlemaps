#import <GoogleMaps/GoogleMaps.h>

@interface TotalPaveTileProvider : GMSSyncTileLayer

- (id _Nonnull)initWithDB:(NSString *_Nonnull)dbName selectQuery:(NSString *_Nonnull)selectQuery scale:(NSArray*_Nonnull)scale error:(NSError*_Nonnull*_Nonnull)error;
- (void)reload:(NSError*_Nonnull*_Nonnull)error;
- (void)reset;
- (NSArray<NSNumber*>*) querySourceData:(NSNumber*)minLon maxLon:(NSNumber*)maxLon minLat:(NSNumber*)minLat maxLat:(NSNumber*)maxLat;
@end
