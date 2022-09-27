
#pragma once

#include <cstdint>
#include <vector>
#include <tp/geom/Extent.h>
#include <tp/qt/QuadTree.h>
#include <tp/GeomType.h>

namespace TP {
    class IGeometry {
        public:
            virtual ~IGeometry() {};

            // virtual uint32_t getNumPoints(void) = 0;
            virtual GeomType getType(void) const = 0;
            virtual geom::Extent<double> calculateExtent(void) = 0;
    };
}
