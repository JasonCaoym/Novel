package com.zydm.base.data.net;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

class MtHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String arg0, SSLSession arg1) {
        return true;
    }

}
