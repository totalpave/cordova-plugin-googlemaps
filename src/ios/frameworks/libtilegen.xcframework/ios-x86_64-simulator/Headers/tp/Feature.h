
#pragma once

#include <cstdint>
#include "./IGeometry.h"
#include <tp/geom/Extent.h>

namespace TP {
    class Feature {
        public:
            Feature(uint32_t id, IGeometry* geometry, double* value);
            virtual ~Feature();

            uint32_t getID(void) const;
            const IGeometry* getGeometry(void) const;
            IGeometry* getGeometry(void);

            const geom::Extent<double>& getExtent(void) const;

            const double* getValue(void) const;

        private:
            uint32_t $id;
            IGeometry* $geometry;
            geom::Extent<double> $extent;
            double* $value;
    };
}
