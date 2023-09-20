
#import "PluginTotalPaveTileLayer.h"
#import "TotalPaveTileProvider.h"

NSString * const PREFIX = @"totalpavetilelayer_";
NSString * const PROPERTY_PREFIX = @"totalpavetilelayer_property";

@implementation PluginTotalPaveTileLayer

-(void)create:(CDVInvokedUrlCommand *)command
{
    NSDictionary* opts = [command.arguments objectAtIndex:1];
    NSString* hashCode = [command.arguments objectAtIndex:2];

    NSString* dbPath = [opts valueForKey:@"dbPath"];
    if ([dbPath isEqual:[NSNull null]] || dbPath == nil) {
        [self.commandDelegate
            sendPluginResult: [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"DB Name is required."]
            callbackId:command.callbackId
        ];
        return;
    }

    NSString* selectQuery = [opts valueForKey:@"selectQuery"];
    if ([selectQuery isEqual:[NSNull null]] || selectQuery == nil) {
        [self.commandDelegate
            sendPluginResult: [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Select Query is required."]
            callbackId:command.callbackId
        ];
        return;
    }

    NSArray* scale = [opts valueForKey:@"scale"];
    if ([scale isEqual:[NSNull null]] || scale == nil) {
        [self.commandDelegate
            sendPluginResult: [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Scale is required."]
            callbackId:command.callbackId
        ];
        return;
    }
    
    [self.commandDelegate runInBackground:^{
        NSError* error;
        TotalPaveTileProvider* provider = [[TotalPaveTileProvider alloc] initWithDB:dbPath selectQuery:selectQuery scale:scale error:&error];
        if (![error isEqual:[NSNull null]] && error != nil) {
            [self.commandDelegate
                sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]]
                callbackId:command.callbackId
            ];
            return;
        }

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
    }];
}

-(void)reload:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        NSError* error;
        [(TotalPaveTileProvider*)[self.mapCtrl.objects objectForKey:[command.arguments objectAtIndex:0]] reload:&error];
        if ([error isEqual:[NSNull null]] || error == nil) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
        }
        else {
            [self.commandDelegate
                sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]]
                callbackId:command.callbackId
            ];
        }
    }];
}

-(void)remove:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSString *providerKey = [command.arguments objectAtIndex:0];
        TotalPaveTileProvider* provider = (TotalPaveTileProvider*)[self.mapCtrl.objects objectForKey:providerKey];
        [provider reset];
        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
            [self.mapCtrl.objects removeObjectForKey:providerKey];

            NSString *propertyId = [providerKey stringByReplacingOccurrencesOfString:PREFIX withString:PROPERTY_PREFIX];
            [self.mapCtrl.objects removeObjectForKey:propertyId];
            provider.map = nil;

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

-(void)querySourceData:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        TotalPaveTileProvider* provider = (TotalPaveTileProvider*)[self.mapCtrl.objects objectForKey:[command.arguments objectAtIndex:0]];
        NSArray<NSNumber*>* output = [provider querySourceData:[command.arguments objectAtIndex:1]
            maxLon:[command.arguments objectAtIndex:2]
            minLat:[command.arguments objectAtIndex:3]
            maxLat:[command.arguments objectAtIndex:4]
        ];
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:output] callbackId:command.callbackId];
    }];
}

-(void)setVisible:(CDVInvokedUrlCommand *)command {
    TotalPaveTileProvider* provider = (TotalPaveTileProvider*)[self.mapCtrl.objects objectForKey:[command.arguments objectAtIndex:0]];
    [provider setOpacity: [(NSNumber*)[command.arguments objectAtIndex:1]  isEqual: @1] ? 1 : 0];
    [self.commandDelegate
        sendPluginResult:[CDVPluginResult
            resultWithStatus:CDVCommandStatus_OK
        ]
        callbackId:command.callbackId
    ];
}

-(void)isVisible:(CDVInvokedUrlCommand *)command {
    TotalPaveTileProvider* provider = (TotalPaveTileProvider*)[self.mapCtrl.objects objectForKey:[command.arguments objectAtIndex:0]];
    [self.commandDelegate
        sendPluginResult:[CDVPluginResult
            resultWithStatus:CDVCommandStatus_OK
            messageAsBool:((int)provider.opacity) == 1
        ]
        callbackId:command.callbackId
    ];
}

@end
