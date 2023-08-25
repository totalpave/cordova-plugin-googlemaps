var utils = require('cordova/utils'),
  Overlay = require('./Overlay');

var TotalPaveTileLayer = function (map, tileLayerOptions, _exec) {
  Overlay.call(this, map, tileLayerOptions, 'TotalPaveTileLayer', _exec, {});
}

utils.extend(TotalPaveTileLayer, Overlay);

// Setup prototype here
// ex: Polyline.prototype.setPoints = function (points) {

TotalPaveTileLayer.prototype.reload = function () {
  return new Promise((resolve, reject) => {
    this.exec.call(this, resolve, reject, this.getPluginName(), 'reload', [this.getId()]);
  });
}

TotalPaveTileLayer.prototype.querySourceData = function(minLon, maxLon, minLat, maxLat) {
  return new Promise((resolve, reject) => {
    this.exec.call(this, resolve, reject, this.getPluginName(), 'querySourceData', [this.getId(), minLon, maxLon, minLat, maxLat]);
  });
}

TotalPaveTileLayer.prototype.remove = function (callback) {
  var self = this;
  if (self._isRemoved) {
    if (typeof callback === 'function') {
      return;
    } else {
      return Promise.resolve();
    }
  }
  Object.defineProperty(self, '_isRemoved', {
    value: true,
    writable: false
  });

  var resolver = function (resolve, reject) {
    self.exec.call(self,
      function () {
        self.destroy();
        resolve.call(self);
      },
      reject.bind(self),
      self.getPluginName(), 'remove', [self.getId()], {
        remove: true
      });
  };

  var result;
  if (typeof callback === 'function') {
    resolver(callback, self.errorHandler);
  } else {
    result = new Promise(resolver);
  }

  if (self.points) {
    self.points.empty();
  }
  self.trigger(self.__pgmId + '_remove');

  return result;
};
module.exports = TotalPaveTileLayer;
  