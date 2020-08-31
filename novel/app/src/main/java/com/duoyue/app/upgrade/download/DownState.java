package com.duoyue.app.upgrade.download;

/**
 * 下载文件返回标识
 * @author victor
 *
 */
public interface DownState {
    	int WAITING = 0;
    	int LINKING = 200;
    	int DOWNLOAD = 300;
    	int NETERROR = 400;
    	int PAUSE = 500;
    	int DELETE = 600;
    	int FILEERROR = 700;
    	int FINISH = 800;
}
 
