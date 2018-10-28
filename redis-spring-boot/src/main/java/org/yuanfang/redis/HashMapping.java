package org.yuanfang.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HashMapping {
    // @Autowired
    // HashOperations<String, byte[], byte[]> hashOperations;
    //
    // HashMapper<Object, byte[], byte[]> mapper = new ObjectHashMapper();
    //
    // public void writeHash(String key, Address address) {
    //     Map<byte[], byte[]> mappedHash = mapper.toHash(address);
    //     hashOperations.putAll(key, mappedHash);
    // }
    //
    // public Address loadHash(String key) {
    //     Map<byte[], byte[]> loadedHash = hashOperations.entries("key");
    //     return (Address) mapper.fromHash(loadedHash);
    // }
}


