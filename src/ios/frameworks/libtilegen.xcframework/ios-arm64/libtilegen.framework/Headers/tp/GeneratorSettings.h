
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
            Defaults to 2.

            Stroke width, after all calculations and modifications have been applied, will never be below minStrokeWidth.
        */
        int minStrokeWidth;
        /**
            Defaults to 3
        */
        int antiAlias;
        /**
            Defaults to 0.1
            Expands (positive) / Shrinks (negative) tile boundary when searching for sections to draw.

            The padding is a percent value of tileSize.
            Tile is expanded/shrunk from the center of the tile (all directions are affected equally).

            This feature exists to fix https://totalpave.atlassian.net/browse/TP-2186; where if a point is draw vey close to the edge of a tile
            and never crosses over to the adjacent tile, the full stroke width will not be drawn.
            This features allows us to expand the adjacent tile's boundary in which data will be searched for, which will include the point that
            is right outside of its unmodified boundary. As a result, it will treat that point's full dataset as inside of its boundary.
            
            This property may influence performance as it manipulates how many data points are considered inside a tile; which changes how much
            rendering work it will do.
            
            Increasing this value may be required if you significantly increase strokeWidth.
            To know for certain you'll have to find or create an situation that match TP-2186.
            1-Data points is very close to tile's normal boundary.
            2-Data points does not cross over to the adjacent tile.
            3-Observed if the point/line width is fully drawn or not.
        */
        float tileLogicPadding;
        /**
            Defaults to 1
        */
        int zoomModifierThreshold;
        /**
            Defaults to 0 (effectively does nothing)

            Calculated Stroke Width: The stroke width that is used in the final result. The calculated stroke width factors in all important values, such as dpi scale and the GeneratorSettings stroke width.

            Reduces calculated stroke width by zoomModifier as a % value.
            Reduction is applied once per zoom level, starting from zoomModifierThreshold.

            Calculated Stroke Width will never be reduced to below minStrokeWidth.

            Example:
            We'll be referring to calculaterd stroke width as csw.
            If zoomModifierThreshold is 15.
            Zoom 16 - No reduction.
            Zoom 15 - csw -= (csw * 0.3) (round up to minStrokeWidth if necessary)
            Zoom 14 - csw -= (csw * 0.6) (round up to minStrokeWidth if necessary)
        */
        float zoomModifier;
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
            GeneratorSettingsBuilder& setTileLogicPadding(const float padding);
            GeneratorSettingsBuilder& setStrokeWidth(const int width);
            GeneratorSettingsBuilder& setMinStrokeWidth(const int width);
            GeneratorSettingsBuilder& setAntiAlias(const int aa);
            GeneratorSettingsBuilder& setZoomModifierThreshold(const int threshold);
            GeneratorSettingsBuilder& setZoomModifier(const float modifier);
            GeneratorSettings build(void) const;

        private:
            std::string $dbPath;
            std::string $sqlString;
            std::vector<Scale::Item> $scale;
            int $dpiScale;
            int $tileSize;
            int $strokeWidth;
            int $minStrokeWidth;
            int $antiAlias;
            float $tileLogicPadding;
            int $zoomModifierThreshold;
            float $zoomModifier;
    };
}
