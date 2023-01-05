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

#include <tp/qt/QuadPoint.h>

namespace TP { namespace qt {
    class XYPoint: public QuadPoint {
        public:
            XYPoint(void);
            XYPoint(double x, double y);
            XYPoint(double x, double y, const void* data);
            virtual ~XYPoint();
            const bool isInBounds(const geom::Extent<double>& extent) const override;
            
            double getX(void) const;
            double getY(void) const;

        private:
            double $x;
            double $y;
    };
}}
