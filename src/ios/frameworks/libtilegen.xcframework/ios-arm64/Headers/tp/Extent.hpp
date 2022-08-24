
#pragma once

#include <limits>

// TODO
// We should probably represent GeoPoints via a class that handles
// the translations/conversions/normalizing
// For now this class assumes that x are longitude and y is latitude and will normalize appropriately

namespace TP {
    template <class T>
    class Extent {
        public:
            Extent(void) {
                T inf, neginf;
                if (std::numeric_limits<T>::has_infinity) {
                    // infinity is only a valid concept on floating
                    // numbers
                    inf = std::numeric_limits<T>::infinity();
                    neginf = -(std::numeric_limits<T>::infinity());
                }
                else {
                    // We must be dealing with an int type
                    inf = std::numeric_limits<T>::max();
                    neginf = std::numeric_limits<T>::min();
                }

                set(
                    inf,
                    inf,
                    neginf,
                    neginf
                );
            }

            Extent(T minx, T miny, T maxx, T maxy) {
                set(minx, miny, maxx, maxy);
            }

            virtual ~Extent() {}

            /**
             * @brief   Returns true if the other extent is completely contained
             *          or overlaps this extent.
             * 
             * @param extent 
             * @return true 
             * @return false 
             */
            virtual bool isInBounds(const Extent& b) const {
                const Extent<T>& a = *this;
                /*
                    Instead of checking if their box is inside our box,
                    which requires 16 across both x & y boolean checks...
                    it's more efficent to check if their box is completely
                    out of bounds of our box, which can be done with only
                    4 boolean checks
                */
                return !(
                    b.$minx > a.$maxx || b.$maxx < a.$minx ||
                    b.$miny > a.$maxy || b.$maxy < a.$miny
                );
            }

            void set(T minx, T miny, T maxx, T maxy) {
                $minx = minx;
                $miny = miny;
                $maxx = maxx;
                $maxy = maxy;
            }

            void get(T& minx, T& miny, T& maxx, T& maxy) const {
                minx = $minx;
                miny = $miny;
                maxx = $maxx;
                maxy = $maxy;
            }

            void extend(T x, T y) {
                if (x < $minx) {
                    $minx = x;
                }
                if (x > $maxx) {
                    $maxx = x;
                }
                if (y < $miny) {
                    $miny = y;
                }
                if (y > $maxy) {
                    $maxy = y;
                }
            }

            void extend(const Extent& extent) {
                extend(extent.$minx, extent.$miny);
                extend(extent.$maxx, extent.$maxy);
            }

            // virtual Extent normalize(void) const {
            //     // By default does nothing, but subclasses may override this
            //     return *this;
            //     // return Extent($minx + 180.0, ($miny * -1.0) + 90, $maxx + 180.0, ($maxy * -1.0)  + 90.0);
            // }

        private:
            T $minx, $miny, $maxx, $maxy;
    };
}
