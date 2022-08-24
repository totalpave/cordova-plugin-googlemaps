
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
    };

    class GeneratorSettingsBuilder {
        public:
            GeneratorSettingsBuilder(void);
            virtual ~GeneratorSettingsBuilder();

            GeneratorSettingsBuilder& setDBPath(const std::string& path);
            GeneratorSettingsBuilder& setSQLString(const std::string& path);
            GeneratorSettingsBuilder& addScaleItem(const Scale::Item& item);
            GeneratorSettings build(void) const;

        private:
            std::string $dbPath;
            std::string $sqlString;
            std::vector<Scale::Item> $scale;
    };
}
