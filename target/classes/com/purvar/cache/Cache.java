package com.purvar.cache;

import java.util.Collection;
import java.util.Map;

/**
 * 
 * 每一个具体的cache必须实现
 * @author mr.liang
 */
public interface Cache<T> {
	
	T get(Object key) ;
	
	void remove(Object key);
	
	void set(Object key, T value);

	void clearAll();
	
	Collection<T> getAll();
	
	Map<Object,T> getCacheAll();
}
