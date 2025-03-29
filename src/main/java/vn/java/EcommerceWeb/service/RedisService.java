package vn.java.EcommerceWeb.service;

import java.util.Set;

public interface RedisService {
    Set<String> getAllKeys();
    Object getValueByKey(String key);
}
