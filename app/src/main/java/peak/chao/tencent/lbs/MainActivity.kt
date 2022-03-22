package peak.chao.tencent.lbs

import android.Manifest
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.tencent.map.geolocation.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requirePermission()
    }

    /**
     * 参考：https://mapapi.qq.com/sdk/locationSDK/Android/doc/index.html
     */
    private fun requestLocation() {
        val mLocationManager = TencentLocationManager.getInstance(this)
        val request = TencentLocationRequest.create()
        //是否允许使用GPS
        request.isAllowGPS = true
        //设置请求级别
        request.requestLevel = TencentLocationRequest.REQUEST_LEVEL_NAME
        //是否需要开启室内定位
        request.isIndoorLocationMode = true
        mLocationManager.requestSingleFreshLocation(null, object : TencentLocationListener {
            /**
             * 位置发生变化.
             */
            override fun onLocationChanged(location: TencentLocation, error: Int, reason: String) {
                Log.e(
                    "onLocationChanged: ",
                    location.toString() + error + reason
                )
                val gson = Gson()
                val mapTx2bd = LatLonUtil.tx2bd(location.longitude, location.latitude)
                val text =
                    "设备是否支持gps：${TencentLocationUtils.isSupportGps(this@MainActivity)}\n是否gps定位：${
                        TencentLocationUtils.isFromGps(location)
                    }\n是否网络定位：${TencentLocationUtils.isFromNetwork(location)}\n${
                        location.toString().replace("TencentLocation{", "").replace("}", "")
                            .replace(",", "\n")
                    }\n转换百度坐标：\n${mapTx2bd[0]},${mapTx2bd[1]}\n错误码：${error}\n错误描述：${reason}"
                findViewById<TextView>(R.id.tv_show).text = text
                Log.e("腾讯坐标: ", "${location.longitude},${location.latitude}")
                Log.e("百度坐标: ", "${mapTx2bd[0]},${mapTx2bd[1]}")
            }

            /**
             * GPS, WiFi, Radio 等状态发生变化.
             */
            override fun onStatusUpdate(name: String, status: Int, desc: String) {
                Log.e("onStatusUpdate: ", name + status + desc)
                findViewById<TextView>(R.id.tv_show).text =
                    "设备名：${name}\n状态码：${status}\n状态描述：${desc}"
            }

        }, Looper.getMainLooper())
    }

    private fun requirePermission() {
        val permissions = arrayOf<String>(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION,  //target为Q时，动态请求后台定位权限
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        XXPermissions.with(this)
            // 申请单个权限
            .permission(permissions)
            // 申请多个权限
            //.permission(Permission.Group.CALENDAR)
            // 设置权限请求拦截器（局部设置）
            //.interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            //.unchecked()
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (all) {
                        requestLocation()
                    } else {
                        Toast.makeText(this@MainActivity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    if (never) {
                        Toast.makeText(this@MainActivity, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT)
                            .show()
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                    } else {
                        Toast.makeText(this@MainActivity, "获取权限失败", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults)
    }
}