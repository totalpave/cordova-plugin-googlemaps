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

#include <cstdint>
#include <vector>
#include <tp/qt/QuadPoint.h>
#include <tp/qt/XYPoint.h>
#include <tp/qt/RectPoint.h>

namespace TP { namespace qt {
    class QuadTreeContext {
        public:
            QuadTreeContext(void);

            static QuadTreeContext* getInstance(void) {
                if ($instance == nullptr) {
                    $instance = new QuadTreeContext();
                }
                return $instance;
            }

            uint32_t getBucketSize(void);
            QuadPoint* getPoint(std::size_t index);

            void setBucketSize(uint32_t size);
            void setData(std::vector<QuadPoint*>* data);
        private:
            uint32_t $size;
            std::vector<QuadPoint*>* $data;

            static QuadTreeContext* $instance;
    };
}}
