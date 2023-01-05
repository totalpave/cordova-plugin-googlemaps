
#pragma once

#include <string>
#include <vector>
#include "./Scale.h"

namespace TP {
    struct GeneratorSettings {
        std::string dbPath;
        
        /**
         * @brief The SQL string must confirm to the following select
         *  SELECT
         *      id (integer),
         *      geometry (GeoJSON.Geometry as string)
         *      value (float)
         * 
         */
        std::string sqlString;
        
        std::vector<Scale::Item> scale;

        /**
         * @brief Scales stroke width for higher resolution screens. Defaults to 1.
        */
        int dpiScale;
        /**
         * @brief Desired resolution of images. Defaults to 256x256. Size is always used for both height and width. 
        */
        int tileSize;
        /**
            Defaults to 2
        */
        int strokeWidth;
        /**
            Defaults to 3
        */
        int antiAlias;
    };

    class GeneratorSettingsBuilder {
        public:
            GeneratorSettingsBuilder(void);
            virtual ~GeneratorSettingsBuilder();

            GeneratorSettingsBuilder& setDBPath(const std::string& path);
            GeneratorSettingsBuilder& setSQLString(const std::string& sql);
            GeneratorSettingsBuilder& addScaleItem(const Scale::Item& item);
            GeneratorSettingsBuilder& setDpiScale(const int scale);
            GeneratorSettingsBuilder& setTileSize(const int size);
            GeneratorSettingsBuilder& setStrokeWidth(const int width);
            GeneratorSettingsBuilder& setAntiAlias(const int aa);
            GeneratorSettings build(void) const;

        private:
            std::string $dbPath;
            std::string $sqlString;
            std::vector<Scale::Item> $scale;
            int $dpiScale;
            int $tileSize;
            int $strokeWidth;
            int $antiAlias;
    };
}
