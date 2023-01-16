
#pragma once

#include <string>
#include <vector>
#include <cstdint>
#include <mutex>
#include <sqlite3.h>
#include <vector>
#include "./IGeometry.h"
#include "./Feature.h"
#include <tp/qt/QuadTree.h>
#include <tp/geom/Extent.h>
#include "./GeneratorSettings.h"
#include "./Scale.h"
#include <shared_mutex>

namespace TP {
    class TileGenerator {
        public:
            static TileGenerator* getInstance(void);

            // bool load(int& errorCode, const std::string& path);
            bool load(int& errorCode, const GeneratorSettings& settings);
            
            // Clears data and invalidates TileGenerator state so that load must be called again.
            void reset();

            int render(std::vector<uint8_t>& buffer, int x, int y, int z);
            void getTileRange(
                const int z,
                uint32_t& minX,
                uint32_t& minY,
                uint32_t& maxX,
                uint32_t& maxY
            ) const;

        private:
            static TileGenerator* $instance;

            std::shared_mutex $mutex;

            sqlite3* $db;

            Scale $scale;
            std::vector<Feature*> $features;
            geom::Extent<double> $networkExtent;
            qt::QuadTree* $quadtree;
            int $tileSize;
            int $dpiScale;
            int $strokeWidth;
            int $antiAlias;
            float $tileLogicPadding;

            // When true, load must be called
            bool $isInvalid;

            TileGenerator(void);
            TileGenerator(const TileGenerator&) = delete;
            TileGenerator(TileGenerator&&) = delete;
            TileGenerator& operator=(const TileGenerator&) = delete;
            TileGenerator& operator=(TileGenerator&&) = delete;

            IGeometry* $parseGeometry(int& errorcode, const unsigned char* strGeom) const;
            void $reportSQLiteError(int code) const;
            void $insertFeatureIntoQuadTree(Feature* feature);
            void $reset();
    };
}
