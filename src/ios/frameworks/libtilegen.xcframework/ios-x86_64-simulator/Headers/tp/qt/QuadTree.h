/*
    Copyright 2022 Total Pave Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

#pragma once

#include <tp/qt/Node.h>
#include <cstdint>
#include <limits>
#include <tp/geom/Extent.h>
#include <vector>
#include <tp/qt/QuadPoint.h>
#include <tp/qt/XYPoint.h>
#include <tp/qt/RectPoint.h>

namespace TP { namespace qt {
    class QuadTree {
        public:
            QuadTree(uint32_t bucketSize, const geom::Extent<double>& extent, uint8_t maxDepth = std::numeric_limits<uint8_t>::max());
            virtual ~QuadTree();

            /**
                Uses XYPoint
            */
            void insert(double x, double y, const void* data);
            void insert(XYPoint* point, const void* data);
            /**
                Uses RectPoint
            */
            void insert(double x1, double y1, double x2, double y2, const void* data);
            void insert(const geom::Extent<double>& extent, const void* data);
            void insert(RectPoint* point, const void* data);
            void query(const geom::Extent<double>& extent, std::vector<const void*>& outData);
        private:
            Node* $root;
            std::vector<QuadPoint*> $data;
    };
}}
