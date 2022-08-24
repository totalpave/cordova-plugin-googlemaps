
#import "PluginTotalPaveTileLayer.h"
#import "TotalPaveTileProvider.h"

NSString * const PREFIX = @"totalPaveTileProvider_";
NSString * const PROPERTY_PREFIX = @"totalPaveTileProvider_property";

@implementation PluginTotalPaveTileLayer

-(void)create:(CDVInvokedUrlCommand *)command
{
    NSDictionary* opts = [command.arguments objectAtIndex:1];
    NSString* hashCode = [command.arguments objectAtIndex:2];
    TotalPaveTileProvider* provider;

    NSString* dbName = [opts valueForKey:@"dbName"];
    if ([dbName isEqual:[NSNull null]]) {
        [self.commandDelegate
            sendPluginResult: [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"DB Name is required."]
            callbackId:command.callbackId
        ];
        return;
    }

    NSString* selectQuery = [opts valueForKey:@"selectQuery"];
    if ([selectQuery isEqual:[NSNull null]]) {
        [self.commandDelegate
            sendPluginResult: [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Select Query is required."]
            callbackId:command.callbackId
        ];
        return;
    }

    NSArray* scale = [opts valueForKey:@"scale"];
    if ([scale isEqual:[NSNull null]]) {
        [self.commandDelegate
            sendPluginResult: [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Scale is required."]
            callbackId:command.callbackId
        ];
        return;
    }
    
    provider = [[TotalPaveTileProvider alloc] initWithDB:dbName selectQuery:selectQuery scale:scale];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        provider.map = self.mapCtrl.map;
        NSString *id = [NSString stringWithFormat:[PREFIX stringByAppendingString:@"%@"], hashCode];
        [self.mapCtrl.objects setObject:provider forKey: id];

        [self.mapCtrl.executeQueue addOperationWithBlock:^{
            NSMutableDictionary *result = [[NSMutableDictionary alloc] init];
            [result setObject:id forKey:@"__pgmId"];
            // NSString *propertyId = [NSString stringWithFormat:[PROPERTY_PREFIX stringByAppendingString:@"%@"], hashCode];
            // NSMutableDictionary *properties = [[NSMutableDictionary alloc] init];
            // // // geodesic
            // // [properties setObject:[NSNumber numberWithBool:polyline.geodesic] forKey:@"geodesic"];
            // [self.mapCtrl.objects setObject:properties forKey:propertyId];

            [self.commandDelegate
                sendPluginResult: [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result]
                callbackId:command.callbackId
            ];
        }];
    });
}

-(void)remove:(CDVInvokedUrlCommand *)command
{
    [self.mapCtrl.executeQueue addOperationWithBlock:^{
        NSString *providerKey = [command.arguments objectAtIndex:0];
        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
            TotalPaveTileProvider* provider = (TotalPaveTileProvider*)[self.mapCtrl.objects objectForKey:providerKey];
            [self.mapCtrl.objects removeObjectForKey:providerKey];

            NSString *propertyId = [providerKey stringByReplacingOccurrencesOfString:PREFIX withString:PROPERTY_PREFIX];
            [self.mapCtrl.objects removeObjectForKey:propertyId];
            provider.map = nil;
            provider = nil;

            [self.commandDelegate
                sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK]
                callbackId:command.callbackId
            ];
        }];
    }];
}

- (void)pluginUnload {
    NSArray *keys = [self.mapCtrl.objects allKeys];
    NSString *key;
    for (int i = 0; i < [keys count]; i++) {
        key = [keys objectAtIndex:i];
        if ([key hasPrefix:PREFIX]) {
            key = [key stringByReplacingOccurrencesOfString:@"_property" withString:@""];
            TotalPaveTileProvider *provider = (TotalPaveTileProvider *)[self.mapCtrl.objects objectForKey:key];
            provider.map = nil;
            provider = nil;
        }
    }
    [self.mapCtrl.objects removeObjectForKey:key];
}

- (void)setPluginViewController:(PluginViewController *)viewCtrl {
  self.mapCtrl = (PluginMapViewController *)viewCtrl;
}

- (void)pluginInitialize
{
  if (self.initialized) {
    return;
  }
  self.initialized = YES;
  [super pluginInitialize];
}

@end
