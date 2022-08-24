
#pragma once

#include <string>
#include <tp/GeomType.h>
#include <tp/IGeometry.h>
#include <vector>
#include <cstdint>
#include <tp/GPoint.h>
#include <CDT.h>

namespace TP {
    class Polygon: public IGeometry {
        public:
            Polygon(const std::string& geoString);
            virtual ~Polygon();

            std::vector<GPoint>& getVertices(void);
            std::vector<GPoint>& getOuterRing(void);
            std::vector<GPoint>& getInnerRing(const std::size_t index);
            std::size_t getInnerRingCount(void) const;
            const CDT::TriangleVec& getTriangles(void) const;
            const CDT::EdgeUSet& getEdges(void) const;

            virtual GeomType getType(void) const override;
            virtual Extent<double> calculateExtent(void) override;

        private:
            std::string $geometryString;
            
            // These are for stroking the boundaries of the polygon, with a solid
            // color stroke. Inner rings are kept separate so that they can have
            // an alternate paint style, if desired.
            std::vector<GPoint> $outRing;
            std::vector<std::vector<GPoint>> $innerRings;
            std::vector<GPoint> $vertices;
            
            // The CDT variables are for triangulation. Fetch the triangles
            // which describes the areas of the polygon that should be filled.
            std::vector<CDT::V2d<double>> $cdtPoints;
            std::vector<CDT::Edge> $cdtEdges;
            
            // Triangulation support
            CDT::Triangulation<double> $cdt;

            void $initGeometry(void);
    };
}
