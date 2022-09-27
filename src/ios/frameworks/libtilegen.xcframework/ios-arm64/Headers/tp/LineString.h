
#pragma once

#include <string>
#include <tp/GeomType.h>
#include <tp/IGeometry.h>
#include <vector>
#include <cstdint>
#include <tp/GPoint.h>
#include <tp/geom/Extent.h>

namespace TP {
    class LineString: public IGeometry {
        public:
            // LineString(const std::vector<std::vector<double>>& geometry);
            LineString(const std::string& geoString);
            virtual ~LineString();

            // void getPoints(std::vector<GPoint*>& out) const;
            std::vector<GPoint>& getPoints(void);
            const std::vector<GPoint>& getPoints(void) const;
            // virtual uint32_t getNumPoints(void) override;
            virtual GeomType getType(void) const override;
            virtual geom::Extent<double> calculateExtent(void) override;

        private:
            std::string $geometryString;
            std::vector<GPoint> $geometry;

            void $initGeometry(void);
    };
}
