/*****************************************************************************
 * Command queue mechanism
 * (Save the number of method executing at the same time)
 * 
 * Changes:
 * Removed most excess variables.
 * Removed multi-getMap call guards. This is an app usage problem.
 * Removed code that discarded calls if passing call count.
 * Refactored "queue" loop to run batches of queued calls before breaking for 50 milliseconds. This should preserve some intended purpose of this queue but may need to be tweaked later.
 * Moved _exec calls to after adding to the command queue and at the end of an _exec call, instead of inside the success and fail callbacks on each item in the queue. Appropriate if-conditions were also added to prevent calling _exec while it is running.
*****************************************************************************/
var cordova_exec = require('cordova/exec'),
  common = require('./Common');

var commandQueue = [];
var _isExecuting = false;
var _isResizeMapExecuting = false;

// This flag becomes true when the page will be unloaded.
var _stopRequested = false;


function execCmd(success, error, pluginName, methodName, args, execOptions) {
  if (_stopRequested) {
    return;
  }

  execOptions = execOptions || {};

  // The JavaScript special keyword 'this' indicates `who call this function`.
  // This execCmd function is executed from overlay instances such as marker.
  // So `this` means `overlay` instance.
  var overlay = this;

  // If the overlay has been already removed from map,
  // do not execute any methods on it.
  if (overlay._isRemoved && !execOptions.remove) {
    console.error('[ignore]' + pluginName + '.' + methodName + ', because removed.');
    return true;
  }

  // If the overlay is not ready in native side,
  // do not execute any methods except remove on it.
  // This code works for map class especially.
  if (!this._isReady && methodName !== 'remove') {
    console.error('[ignore]' + pluginName + '.' + methodName + ', because it\'s not ready.');
    return true;
  }

  // Push the method into the commandQueue(FIFO) at once.
  commandQueue.push({
    'execOptions': execOptions,
    'args': [
      function() {
        //-------------------------------
        // success callback
        //-------------------------------

        if (methodName === 'resizeMap') {
          _isResizeMapExecuting = false;
        }
      
        if (_stopRequested) {
          return;
        }

        if (success) {
          var results = Array.prototype.slice.call(arguments, 0);
          ((overlay, results) => {
            success.apply(overlay,results);
          })(overlay, results);
        }
      },
      function() {
        //-------------------------------
        // error callback
        //-------------------------------

        if (methodName === 'resizeMap') {
          _isResizeMapExecuting = false;
        }
      
        if (_stopRequested) {
          return;
        }

        if (error) {
          var results = Array.prototype.slice.call(arguments, 0);
          ((overlay, results) => {
            error(overlay,results);
          })(overlay, results);

          common.nextTick(function() {
            error.apply(overlay,results);
          });
        }
      },
      pluginName, methodName, args]
  });

  if (!_isExecuting) {
    common.nextTick(_exec);
  }
}
function _exec() {
  if (commandQueue.length === 0) {
    _isExecuting = false;
    return;
  }
  _isExecuting = true;

  var batchCounter = 0;

  while (commandQueue.length > 0 && batchCounter < 100) {
    batchCounter++;
    var commandParams = commandQueue.shift();
    var methodName = commandParams.args[3];

    // If the request is `map.refreshLayout()` and another `map.refreshLayout()` is executing,
    // skip it.
    // This prevents to execute multiple `map.refreshLayout()` at the same time.
    if (methodName === 'resizeMap') {
      if (_isResizeMapExecuting) {
        continue;
      }
      _isResizeMapExecuting = true;
    }

    // If the `_stopRequested` flag is true,
    // do not execute any statements except `remove()` or `clear()` methods.
    if (_stopRequested && (!commandParams.execOptions.remove || methodName !== 'clear')) {
      continue;
    }

    // Some methods have to block other execution requests, such as `map.clear()`
    if (commandParams.execOptions.sync) {
      cordova_exec.apply(this, commandParams.args);
      break;
    }
    cordova_exec.apply(this, commandParams.args);
  }

  setTimeout(function() {
    common.nextTick(_exec);
  }, 50);
}


//----------------------------------------------------
// Stop all executions if the page will be closed.
//----------------------------------------------------
function stopExecution() {
  // Request stop all tasks.
  _stopRequested = true;
}
window.addEventListener('unload', stopExecution);

module.exports = execCmd;
