
#pragma once

#include <vector>
#include <cstdint>
#include <limits>

namespace TP {
    class Scale {
        public:
            struct Item {
                double low = -std::numeric_limits<double>::infinity();
                double high = std::numeric_limits<double>::infinity();
                uint32_t strokeColor = 0x000000FF;
                uint32_t fillColor = 0x000000FF;
            };

            struct ColorInfo {
                uint32_t strokeColor = 0x000000FF;
                uint32_t fillColor = 0x00000080; // 50% opacity
            };

            Scale(void);
            virtual ~Scale();

            void reset(void);
            void addItem(const Item& item);

            ColorInfo getColor(const double value) const;
            uint32_t getStrokeColor(const double value) const;
            uint32_t getFillColor(const double value) const;

        private:
            std::vector<Item> $items;
    };
}
