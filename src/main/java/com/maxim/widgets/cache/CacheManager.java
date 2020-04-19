package com.maxim.widgets.cache;

import com.maxim.widgets.models.Widget;

import java.util.*;


public class CacheManager {

    private static CacheManager instance;
    private static final Object monitor = new Object();
    private Map<Long, Widget> cache = Collections.synchronizedMap(new LinkedHashMap<>());

    private CacheManager() {
    }

    public Widget put(Long cacheKey, Widget value) {
        return cache.put(cacheKey, value);
    }

    public Widget get(Long cacheKey) {
        return cache.get(cacheKey);
    }

    public List<Widget> getAllPageValues(Short page, Short size) {
        return getPage(getAllValues(), page, size);
    }

    public List<Widget> getAllValues() {
        return new ArrayList<>(cache.values());
    }

    public static List<Widget> getPage(List<Widget> widgetList, Short page, Short pageSize) {
        if(pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if(widgetList == null || widgetList.size() < fromIndex){
            return Collections.emptyList();
        }

        // toIndex exclusive
        return widgetList.subList(fromIndex, Math.min(fromIndex + pageSize, widgetList.size()));
    }

    public Set<Map.Entry<Long, Widget>> getSetOfMap() {
        return cache.entrySet();
    }

    public void clear(Long widgetKey) {
        cache.remove(widgetKey);
    }

    public void clearAll() {
        cache.clear();
    }

    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }
}
