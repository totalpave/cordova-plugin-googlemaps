
#pragma once

namespace TP {
    namespace ErrorCode {
        const int DATASET_LOAD_ERROR        = 1;
        const int INVALID_FEATURE           = 2;
        const int UNSUPPORTED_GEOMETRY      = 3;

        // These errors are recoverable. Catch them and adjust your code accordingly.
        const int NO_FEATURES_TO_RENDER     = 4;
        const int TILE_UNAVAILABLE          = 5;
    }
}
