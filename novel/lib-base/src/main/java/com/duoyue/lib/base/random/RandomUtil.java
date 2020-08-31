package com.duoyue.lib.base.random;

import java.util.*;

public class RandomUtil {
	
	public static <T> T getRandom(List<T> resultList){
		Random rand = new Random();
		return resultList.get(rand.nextInt(resultList.size()));
	}
	
	public static <T extends RandomService> T getRandomGroupAD(List<T> list) {
		return getRandomGroupAD(list, new PowConversion());
	}
	
	public static <T extends RandomService> T getRatioRandomGroupAD(List<T> list) {
		return getRandomGroupAD(list, new RatioConversion());
	}

	public static <T extends RandomService> T getRandomGroupAD(List<T> paramList,
			WeightConversion weightConversion) {
		if (paramList != null && paramList.size() > 0) {
			if (paramList.size() == 1) {
				return paramList.get(0);
			}
			List<T> list=new ArrayList<T>(paramList);
			Collections.sort(list, new Comparator<T>() {
				@Override
				public int compare(T o1, T o2) {
					return o1.publishPriority()-o2.publishPriority();
				}
			});
			List<T> randomList = new ArrayList<T>();
			int total = 0;
			int maxPriority = 0;
			for (int i = 0; i < list.size(); i++) {
				T ad = list.get(i);
				// 服务端优先级
				if (maxPriority < ad.publishPriority()) {
					maxPriority = ad.publishPriority();
				}
				if (ad.publishPriority() < maxPriority) {
					break;
				}
				randomList.add(ad);
				total += weightConversion
						.conversionWeight(ad.publishPriority());
			}
			T obj = null;
			if (randomList.size() == 1) {
				obj = list.get(0);
			} else {
				Random rand = new Random();
				int random = rand.nextInt(total);
				int size = randomList.size();
				int sum = 0;
				int index = 0; // 随机到的广告index
				obj = randomList.get(index);
				for (int i = 0; i < size; i++) {
					sum += weightConversion.conversionWeight(randomList.get(i)
							.publishPriority());
					if (random < sum) {
						index = i;
						obj = randomList.get(i);
						break;
					}
				}
			}
			list.remove(obj);
			return obj;
		}
		return null;
	}

}

class PowConversion implements WeightConversion {
	@Override
	public int conversionWeight(int priority) {
		if(priority<5){
			return 1;
		}
		return (int)Math.pow(2,priority-5);		
	}

}

class RatioConversion implements WeightConversion {
	@Override
	public int conversionWeight(int priority) {
		return priority;
	}

}
