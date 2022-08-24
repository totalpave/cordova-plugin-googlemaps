
#pragma once

#include <cstdint>
#include "./IGeometry.h"
#include "./Extent.hpp"

namespace TP {
    class Feature {
        public:
            Feature(uint32_t id, IGeometry* geometry, double* value);
            virtual ~Feature();

            uint32_t getID(void) const;
            const IGeometry* getGeometry(void) const;

            const Extent<double>& getExtent(void) const;

            const double* getValue(void) const;

        private:
            uint32_t $id;
            IGeometry* $geometry;
            Extent<double> $extent;
            double* $value;
    };
}
