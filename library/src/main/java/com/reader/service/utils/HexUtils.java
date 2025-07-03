package com.reader.service.utils;

/**
 * 16进制转换的工具类
 */
public class HexUtils {

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static  byte[] hexStringToBytes(String hexString) {
        String hex = "0123456789ABCDEF";
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            byte b1 = (byte) hex.indexOf(hexChars[pos]);
            byte b2 = (byte) hex.indexOf(hexChars[pos + 1]);
            d[i] = (byte) (b1 << 4 | b2);
        }
        return d;
    }

    /* Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "";
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }


    /**
     * 16进制转10进制
     *
     * @param hex 16进制
     * @param startIndex 起始下标
     * @param len 长度, 按字符算
     * @return 10进制
     */
    public static long hexToLong(byte[] hex, int startIndex, int len) {
        long ret = 0;

        final int e = startIndex + len;
        for (int i = startIndex; i < e; ++i) {
            ret <<= 8;
            ret |= hex[i] & 0xFF;
        }
        return ret;
    }
    /**
     * 16进制转10进制 (小端模式)
     *
     * @param b 16进制
     * @param startIndex 起始下标
     * @param len 长度, 按字符算
     * @return 10进制
     */
    public static long hexToLongLE(byte[] b, int startIndex, int len) {
        long ret = 0;
        for (int i = startIndex + len - 1; i >= startIndex; --i) {
            ret <<= 8;
            ret |= b[i] & 0xFF;
        }
        return ret;
    }
    /**
     * 16进制转10进制 (小端模式)
     *
     * @param b 16进制
     * @param startIndex 起始下标
     * @param len 长度, 按字符算
     * @return 10进制
     */
    public static int hexToIntLE(byte[] b, int startIndex, int len) {
        int ret = 0;
        for (int i = startIndex + len - 1; i >= startIndex; --i) {
            ret <<= 8;
            ret |= b[i] & 0xFF;
        }
        return ret;
    }
    /**
     * 八字节转小端格式
     *
     * @param i
     * @return
     */
    public static byte[] intToLE8ByteArray(long i) {
        byte[] result = new byte[8];
        // 由高位到低位
        result[7] = (byte) ((i >> 56) & 0xFF);
        result[6] = (byte) ((i >> 48) & 0xFF);
        result[5] = (byte) ((i >> 40) & 0xFF);
        result[4] = (byte) ((i >> 32) & 0xFF);
        result[3] = (byte) ((i >> 24) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }
    /**
     * 四字节转小端格式
     *
     * @param i
     * @return
     */
    public static byte[] intToLE4ByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[3] = (byte) ((i >> 24) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 两个字节的小端转换
     *
     * @param i
     * @return
     */
    public static byte[] intToLE2Bytes(int i) {
        byte[] result = new byte[2];
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }
    /**
     * 两个字节的小端转换
     *
     * @param i
     * @return
     */
    public static byte[] intToLEBytes(int i) {
        byte[] result = new byte[1];
        result[0] = (byte) (i & 0xFF);
        return result;
    }
//    /**
//     * 将字符串转换成16进制
//     *
//     * @param message
//     * @return
//     */
//    public static byte[] getHexBytes(String message) {
//        int len = message.length() / 2;
//        char[] chars = message.toCharArray();
//        String[] hexStr = new String[len];
//        byte[] bytes = new byte[len];
//        try {
//            for (int i = 0, j = 0; j < len; i += 2, j++) {
//                hexStr[j] = "" + chars[i] + chars[i + 1];
//                bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bytes;
//    }
//
}
