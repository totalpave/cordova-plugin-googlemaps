
#pragma once

#include <string>
#include <png.h>
#include <cstdint>
#include <vector>

namespace TP {
    class Image {
        public:
            struct XY {
                int32_t x = 0;
                int32_t y = 0;
            };

            struct XYRelative {
                float x = 0.0f;
                float y = 0.0f;
            };

            Image(uint16_t width, uint16_t height, uint32_t fillColor = 0x00000000, uint8_t antiAliasing = 1);
            virtual ~Image();

            void setStrokeColor(uint32_t color);
            void setStrokeWidth(uint8_t size);
            void setFillColor(uint32_t color);

            void drawPoint(const XY& point);
            void drawPoint(const XYRelative& point);
            void drawLine(const XY& from, const XY& to);
            void drawLine(const XYRelative& from, const XYRelative& to);
            void drawTriangle(const XY& v1, const XY& v2, const XY& v3);
            void drawTriangle(const XYRelative& v1, const XYRelative& v2, const XYRelative& v3);

            int write(const std::string& path) const;
            int render(std::vector<uint8_t>& buffer) const;

        private:
            uint16_t $targetWidth;
            uint16_t $targetHeight;
            uint32_t $width;
            uint32_t $height;
            uint32_t $strokeColor;
            uint8_t $strokeWidth;
            uint32_t $fillColor;
            uint32_t** $data;
            uint8_t $aaSamples;
            uint32_t $initialBaseColor;

            uint32_t $swapBytes(uint32_t x) const;
            int32_t $clamp(int32_t value, int32_t minimum, int32_t maximum);
            double $clamp(double value, double minimum, double maximum);

            void $ssaa(uint32_t** outData) const;

            void $drawPoint(const XY& point, uint32_t color, uint8_t strokeWidth, bool blendColor = false);

            Image::XY $resolveRelative(const Image::XYRelative& point) const;
            bool $isPointInTriangle(const Image::XY& v1, const Image::XY& v2, const Image::XY& v3, const Image::XY& p) const;
    };
}
