package com.purvar.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author liang
 */
public class LocalCache<T> implements Cache<T>{
	
	/**
	 * 不能用static ,因为 key可能会发生冲突
	 */
	private final Map<Object,T> map = new ConcurrentHashMap<Object,T>();

	@Override 
	public T get(Object key) {
		assertNotNull(key, "getKey cannot be null");
		return (T) map.get(key);
	}

	@Override
	public void remove(Object key) {
		assertNotNull(key, "removeKey cannot be null");
		map.remove(key);
	}

	@Override
	public void set(Object key, T value) {
		assertNotNull(key, "setKey cannot be null");
		assertNotNull(value, "setValue cannot be null");
		
		map.put(key, value);
	}
	
	@Override
	public void clearAll() {
		map.clear();
	}
	
	/**
	 * 断言不为null
	 */
	public static void assertNotNull(Object value,Object msg){
		if(value == null){
			throw new RuntimeException(msg.toString());
		}
	}

	@Override
	public Collection<T> getAll() {
		return map.values();
	}

	@Override
	public Map<Object, T> getCacheAll() {
		return map;
	}
}
