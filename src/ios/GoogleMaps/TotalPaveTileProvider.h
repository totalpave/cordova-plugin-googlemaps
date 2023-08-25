#import <GoogleMaps/GoogleMaps.h>

@interface TotalPaveTileProvider : GMSSyncTileLayer

- (id _Nonnull)initWithDB:(NSString *_Nonnull)dbName selectQuery:(NSString *_Nonnull)selectQuery reloadSelectQuery:(NSString *_Nonnull)reloadSelectQuery scale:(NSArray*_Nonnull)scale error:(NSError*_Nonnull*_Nonnull)error;
- (void)reload:(NSError*_Nonnull*_Nonnull)error;
- (void)reload:(NSError*_Nonnull*_Nonnull)error ids:(NSArray*_Nonnull)ids;
- (void)reset;
- (NSArray<NSNumber*>*_Nonnull) querySourceData:(NSNumber*_Nonnull)minLon maxLon:(NSNumber*_Nonnull)maxLon minLat:(NSNumber*_Nonnull)minLat maxLat:(NSNumber*_Nonnull)maxLat;
@end
