
#pragma once

#include <cstdint>
#include "./Image.h"
#include "./Extent.hpp"
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

            static void calculateExtent(const int16_t& x, const int16_t& y, const int16_t& z, double& minlon, double&minlat, double& maxlon, double& maxlat);
            static void calculateExtent(const int16_t& x, const int16_t& y, const int16_t& z, Extent<double>& extent);

            bool raster(int& errorCode, std::vector<Feature*>& features, Image& image, const Scale& scale);

        private:
            uint32_t $x, $y, $z;
            uint16_t $tileSize;
            double $initialResolution;
            double $resolution;
            MPoint $normal;
            MPoint $offset;
            Extent<double> $extent;

            double $getResZ(const uint32_t& z) const;
            MPoint $gToM(const GPoint& g) const;
            Image::XY $mToXY(const MPoint& m) const;
            Image::XYRelative $mToXYRelative(const MPoint& m) const;
            MPoint $pToM(const Image::XY& p) const;
            MPoint $mToP(const MPoint& m) const;
            MPoint $pToL(const MPoint& m) const;

            Extent<double> $normalizeGeoExtent(const Extent<double>& extent) const;

            // bool $isPointInTriangle(const Image::XY& v1, const Image::XY& v2, const Image::XY& v3, const Image::XY& p) const;

            std::vector<MPoint> $batchConvert(const std::vector<GPoint>& points) const;

            bool $rasterFeature(int& errorCode, Feature* feature, Image& image, const Scale& scale);
            bool $rasterLine(int& errorCode, Feature* feature, LineString* geometry, Image& image, const Scale& scale);
            bool $rasterPolygon(int& errorCode, Feature* feature, Polygon* geometry, Image& image, const Scale& scale);
    };
}
