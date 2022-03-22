package peak.chao.tencent.lbs

object LatLonUtil {
    /**
     * 坐标转换，腾讯地图转换成百度地图坐标
     *
     * @param lat 腾讯纬度
     * @param lon 腾讯经度
     * @return 返回结果：经度,纬度
     */
    fun tx2bd(lon: Double, lat: Double): DoubleArray {
        val bd_lon: Double
        val bd_lat: Double
        val x_pi = 3.14159265358979324
        val z = Math.sqrt(lat * lat + lon * lon) + 0.00002 * Math.sin(lon * x_pi)
        val theta = Math.atan2(lon, lat) + 0.000003 * Math.cos(lat * x_pi)
        bd_lat = z * Math.cos(theta) + 0.0065
        bd_lon = z * Math.sin(theta) + 0.006
        return doubleArrayOf(bd_lon, bd_lat)
    }

    /**
     * 坐标转换，百度地图坐标转换成腾讯地图坐标
     *
     * @param lat 百度坐标纬度
     * @param lon 百度坐标经度
     * @return 返回结果：纬度,经度
     */
    fun bd2tx(lon: Double, lat: Double): DoubleArray {
        val tx_lon: Double
        val tx_lat: Double
        val x_pi = 3.14159265358979324
        val x = lat - 0.0065
        val y = lon - 0.006
        val z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi)
        val theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi)
        tx_lat = z * Math.cos(theta)
        tx_lon = z * Math.sin(theta)
        return doubleArrayOf(tx_lon, tx_lat)
    }
}