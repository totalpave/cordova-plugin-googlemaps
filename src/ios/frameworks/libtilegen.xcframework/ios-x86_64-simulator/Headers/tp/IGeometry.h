
#pragma once

#include <cstdint>
#include <vector>
#include <tp/Extent.hpp>
#include <tp/GeomType.h>

namespace TP {
    class IGeometry {
        public:
            virtual ~IGeometry() {};

            // virtual uint32_t getNumPoints(void) = 0;
            virtual GeomType getType(void) const = 0;
            virtual Extent<double> calculateExtent(void) = 0;
    };
}
