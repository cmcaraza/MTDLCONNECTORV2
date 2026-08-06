/*
 * UNCLASSIFIED
 *
 * Revision History:
 *
 *   Date     SEC.No.  Programmer           Description
 * ---------- ------- -------------------- -------------------------------------
 * 2026-07-31 IMDL001  Zopiloman            Initial release
 *
 */
package mx.sedena.itzamna.mtdlconnector.core;

public class ModeC {

    /**
     * @param altitude_in_feet
     * @return altitude in hundreds of feet, as reordered Gillham code
     */
    public static int encode(int altitude_in_feet) {
        if (altitude_in_feet == 0) {
            return 0x0fff;
        }
        int temp = toGillhamCode(altitude_in_feet);
        temp = rotateLeft(temp);
        return swap(temp);
    }

    /**
     * @param altitude_in_feet
     *           as reordered Gillham code
     * @return altitude in feet
     */
    public static int decode(int altitude_in_feet) {
        if (altitude_in_feet == 0x0fff) {
            return 0;
        }
        int altitude_in_hundreds = altitude_in_feet / 100;
        int temp = rotateRight(altitude_in_hundreds);
        temp = swap(temp);
        return fromGillhamCode(temp);
    }

    private static final int LOOKUP[] = { 0x01, 0x03, 0x02, 0x06, 0x04, 0x04,
            0x06, 0x02, 0x03, 0x01 };

    private static final int zero_adjust = -1200;
    private static final int height_lsb = 100;
    private static final int round_factor = 50;

    /**
     * @param altitude_in_feet
     * @return altitude in hundreds of feet, as Gillham code
     */
    // Heller's algorithm
    private static int toGillhamCode(int altitude_in_feet) {
        if (altitude_in_feet != 0) {
            int adj_altitude = ((altitude_in_feet - zero_adjust) + round_factor) /
                    height_lsb;
            int high_gray = adj_altitude / 5;
            return (((high_gray ^ (high_gray >>> 1)) << 3) | LOOKUP[(adj_altitude % 10)]);
        }
        return 0;
    }

    /**
     * @param altitude_in_hundreds
     *           , as Gillham code
     * @return altitude in feet
     */
    // Wikipedia article on Gillham code
    private static int fromGillhamCode(int altitude_in_hundreds) {
        if (altitude_in_hundreds != 0) {
            int fiveHundreds = grayToBinary(altitude_in_hundreds >>> 3);
            int oneHundreds = grayToBinary(altitude_in_hundreds & 0x07);

            if (oneHundreds == 7) {
                oneHundreds = 5;
            }
            if (fiveHundreds % 2 != 0) {
                oneHundreds = 6 - oneHundreds;
            }
            return ((fiveHundreds * 500) + (oneHundreds * 100)) - 1300;
        }
        return 0;
    }

    private static int grayToBinary(int val) {
        int temp = val;
        temp ^= (temp >>> 8);
        temp ^= (temp >>> 4);
        temp ^= (temp >>> 2);
        temp ^= (temp >>> 1);
        return temp;
    }

    /*
     * Convert between
     *
     * D1 D2 D4 A1 A2 A4 B1 B2 B4 C1 C2 C4 - Gillham
     *
     * and
     *
     * A4 A2 A1 B4 B2 B1 C4 C2 C1 D4 D2 D1 - SIVA
     */

    // octal constants
    private static final int ALL_BITS = 07777;
    private static final int LEFT_SIDE = 07000;
    private static final int RIGHT_SIDE = 00007;

    /**
     * Given Ax Ay Az Bx By Bz Cx Cy Cz Dx Dy Dz
     *
     * @param bits
     * @return Bx By Bz Cx Cy Cz Dx Dy Dz Ax Ay Az
     */
    private static int rotateLeft(int bits) {
        int temp = bits & ALL_BITS;
        temp <<= 3;
        temp |= ((bits & LEFT_SIDE) >> 9);
        return temp;
    }

    /**
     * Given Ax Ay Az Bx By Bz Cx Cy Cz Dx Dy Dz
     *
     * @param bits
     * @return Dx Dy Dz Ax Ay Az Bx By Bz Cx Cy Cz
     */
    private static int rotateRight(int bits) {
        int temp = bits & ALL_BITS;
        temp >>= 3;
        temp |= ((bits & RIGHT_SIDE) << 9);
        return temp;
    }

    // octal constants
    private static final int LEFT_BITS = 04444;
    private static final int RIGHT_BITS = 01111;
    private static final int CENTER_BITS = 02222;

    /**
     * Given Ax Ay Az Bx By Bz Cx Cy Cz Dx Dy Dz
     *
     * @param bits
     * @return Az Ay Ax Bz By Bx Cz Cy Cx Dz Dy Dx
     */
    private static int swap(int bits) {
        int leftBits = (bits & RIGHT_BITS) << 2;
        int rightBits = (bits & LEFT_BITS) >> 2;
        int centerBits = bits & CENTER_BITS;
        return leftBits | centerBits | rightBits;
    }

    public static void main(String[] args) {
        System.out.println("0 :" + decode(encode(0)));
        System.out.println("200 :" + decode(encode(200) * 100));
        System.out.println("1100 :" + decode(encode(1100) * 100));
        System.out.println("1200 :" + decode(encode(1200) * 100));
        System.out.println("1300 :" + decode(encode(1300) * 100));
        System.out.println("1400 :" + decode(encode(1400) * 100));
        System.out.println("1500 :" + decode(encode(1500) * 100));
        System.out.println("1600 :" + decode(encode(1600) * 100));
        System.out.println("1700 :" + decode(encode(1700) * 100));
        System.out.println("1800 :" + decode(encode(1800) * 100));
        System.out.println("1900 :" + decode(encode(1900) * 100));
        System.out.println("10000 :" + decode(encode(10000) * 100));
        System.out.println("11000 :" + decode(encode(11000) * 100));
        System.out.println("11100 :" + decode(encode(11100) * 100));
        System.out.println("11149 :" + decode(encode(11149) * 100));
        System.out.println("11150 :" + decode(encode(11150) * 100));
    }
}

// UNCLASSIFIED