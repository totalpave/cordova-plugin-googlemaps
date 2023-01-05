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

#include <limits>
#include <cstdint>

namespace TP { namespace geom {
    template <class T>
    class Extent {
        public:
            Extent(void);

            Extent(T minx, T miny, T maxx, T maxy);

            virtual ~Extent();// {}

            /**
             * @brief   Returns true if the other extent is completely contained
             *          or overlaps this extent.
             * 
             * @param extent 
             * @return true 
             * @return false 
             */
            virtual bool isInBounds(const Extent& b) const;
            virtual bool isInBounds(const T x, const T y) const;

            void set(T minx, T miny, T maxx, T maxy);

            void get(T& minx, T& miny, T& maxx, T& maxy) const;

            void getRange(T& x, T& y) const;
            void translate(T x, T y);
            void extend(T x, T y);

            void extend(const Extent<T>& extent);

            static Extent<T> quad(const Extent<T>& extent);

        private:
            T $minx, $miny, $maxx, $maxy;
    };
}}
