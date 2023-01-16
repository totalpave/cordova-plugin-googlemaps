
#pragma once

#include <cstdint>
#include "./Image.h"
#include <tp/geom/Extent.h>
#include "./GPoint.h"
#include <vector>
#include "./Feature.h"
#include "./MPoint.h"
#include "./Scale.h"
#include <tp/LineString.h>
#include <tp/Polygon.h>

namespace TP {
    class Tile {
        public:
            Tile(uint16_t tileSize, uint32_t x, uint32_t y, uint32_t z);
            virtual ~Tile();

            static void calculateExtent(const uint32_t& x, const uint32_t& y, const uint32_t& z, double& minlon, double&minlat, double& maxlon, double& maxlat);
            static void calculateExtent(const uint32_t& x, const uint32_t& y, const uint32_t& z, geom::Extent<double>& extent);
            static void calculateExtent(const uint32_t& x, const uint32_t& y, const uint32_t& z, geom::Extent<double>& extent, float modifier);

            bool raster(int& errorCode, std::vector<Feature*>& features, Image& image, const Scale& scale);

        private:
            uint32_t $x, $y, $z;
            uint16_t $tileSize;
            double $initialResolution;
            double $resolution;
            MPoint $normal;
            MPoint $offset;
            geom::Extent<double> $extent;

            double $getResZ(const uint32_t& z) const;
            MPoint $gToM(const GPoint& g) const;
            Image::XYRelative $mToXYRelative(const MPoint& m) const;

            geom::Extent<double> $normalizeGeoExtent(const geom::Extent<double>& extent) const;

            std::vector<MPoint> $batchConvert(const std::vector<GPoint>& points) const;

            bool $rasterFeature(int& errorCode, Feature* feature, Image& image, const Scale& scale);
            bool $rasterLine(int& errorCode, Feature* feature, LineString* geometry, Image& image, const Scale& scale);
            bool $rasterPolygon(int& errorCode, Feature* feature, Polygon* geometry, Image& image, const Scale& scale);
    };
}
