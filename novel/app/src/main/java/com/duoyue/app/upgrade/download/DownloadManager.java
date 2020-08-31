package com.duoyue.app.upgrade.download;

import android.os.Handler;
import android.text.TextUtils;
import com.duoyue.app.upgrade.download.DownState;
import com.duoyue.app.upgrade.download.DownTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 下载线程管理
 * @author fanwentao
 */
public class DownloadManager {
	private static DownloadManager downloadManager = null;
	// 设置最大并发下载数
	private static int maxCounts = 2;
	private static int currentCounts = 0;
	private RunnableThread runnableThread = null;
	private Map<String, DownTask> taskMap = null;
	private ExecutorService executorService = Executors.newFixedThreadPool(2);
	
	
	private DownloadManager(){
		taskMap = new ConcurrentHashMap<>();
		runnableThread = new RunnableThread();
	}
	
	public static DownloadManager getInstance(){
		if(null == downloadManager){
			downloadManager = new DownloadManager();
		}
		return downloadManager;
	}
	
	// 添加任务自动执行任务
	public void insertTask(DownTask task){
		if(task.getBean() != null){
			String url = task.getBean().getUrl();
			if(!taskMap.containsKey(url)){
				taskMap.put(url, task);
				if(task.getState() == DownState.WAITING){
					if(currentCounts < maxCounts){
						executorService.execute(runnableThread);
					}
				}
			}
		}
	}
	
	// 设置任务状态
	public void setState(final String url, int state){
		if(!taskMap.isEmpty()){
			if(taskMap.get(url) != null){
				taskMap.get(url).setState(state);
				if(currentCounts < maxCounts){
					executorService.execute(runnableThread);
				}
			}
		}
	}
		
	private String getFreeTaskKey(){
		if(!taskMap.isEmpty()){
			for(String key : taskMap.keySet()) {
				if(taskMap.get(key).getState() == DownState.WAITING){
					taskMap.get(key).setState(DownState.LINKING);
					return key;
				}
			}
		}
		return null;
	}
	
	// 获取任务队列中的任务
	public DownTask getDownTask(String url){
		DownTask downTask = null;
		if(!taskMap.isEmpty() && taskMap.containsKey(url)){
			downTask = taskMap.get(url);
		}
		return downTask;
	}
	
	// 设置任务消息回调
	public void setTaskHandle(String url, Handler handler){
		if(!taskMap.isEmpty()){
			if(taskMap.get(url) != null){
				taskMap.get(url).setHandle(handler);
			}
		}
	}
	
	// 移除存在任务队列里的某个任务
	public void removeTask(String key){
		if(!taskMap.isEmpty()){
			taskMap.remove(key);
		}
	}
	
	private class RunnableThread implements Runnable{

		@Override
		public void run() {
			try {
				while(true){
					if(taskMap.isEmpty()){
						break;
					}else{
						if(currentCounts < maxCounts){
							String taskKey = getFreeTaskKey();
							if(TextUtils.isEmpty(taskKey)){
								break;
							}else{
								currentCounts++;
								if(taskMap.get(taskKey).doWork()){
									removeTask(taskKey);
								}
								currentCounts--;
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
