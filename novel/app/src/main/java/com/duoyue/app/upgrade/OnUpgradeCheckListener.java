package com.duoyue.app.upgrade;

import com.duoyue.app.upgrade.download.UpgradeMsgBean;

public interface OnUpgradeCheckListener {

    boolean onCheckFinish(int code, UpgradeMsgBean data);
}
