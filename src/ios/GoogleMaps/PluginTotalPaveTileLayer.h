
#import "CordovaGoogleMaps.h"
#import "IPluginProtocol.h"

@interface PluginTotalPaveTileLayer : CDVPlugin<IPluginProtocol>
@property (nonatomic, strong) PluginMapViewController* mapCtrl;
@property (nonatomic) BOOL initialized;

- (void)reload:(CDVInvokedUrlCommand*)command;
- (void)create:(CDVInvokedUrlCommand*)command;
- (void)remove:(CDVInvokedUrlCommand*)command;
- (void)setVisible:(CDVInvokedUrlCommand*)command;
- (void)querySourceData:(CDVInvokedUrlCommand*)command;
- (void)isVisible:(CDVInvokedUrlCommand*)command;

@end
