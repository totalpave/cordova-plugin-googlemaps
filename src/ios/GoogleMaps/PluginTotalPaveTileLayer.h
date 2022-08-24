
#import "CordovaGoogleMaps.h"
#import "IPluginProtocol.h"

@interface PluginTotalPaveTileLayer : CDVPlugin<IPluginProtocol>
@property (nonatomic, strong) PluginMapViewController* mapCtrl;
@property (nonatomic) BOOL initialized;

- (void)create:(CDVInvokedUrlCommand*)command;
- (void)remove:(CDVInvokedUrlCommand*)command;

@end
