package cn.edu.pku.wangyongsheng.util;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by xiaosheng on 2017/10/11.
 */

public class NetUtil {
    public static final int NETWORK_NONE=0;
    public static final int NETWORK_WIFI=1;
    public static final int NETWORK_MOBILE=2;
    //使用ConnectivityManager类管理网络状态
    public static int getNetworkState(Context context){
        ConnectivityManager connManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkType =connManager.getActiveNetworkInfo();
        if (networkType==null){
            return NETWORK_NONE;
        }
        int ntype=networkType.getType();
        if (ntype==ConnectivityManager.TYPE_MOBILE){
            return NETWORK_MOBILE;
        }
        if (ntype==ConnectivityManager.TYPE_WIFI){
            return NETWORK_WIFI;
        }
        return NETWORK_NONE;
    }
}
