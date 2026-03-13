package com.example.kitchensink.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class BlacklistToken {

    Set<String> set = new HashSet<>();

    public boolean isBlacklisted(String token){
        return set.contains(token);
    }

    public void blacklistToken(String token){
        set.add(token);
    }

    public void removeToken(String token){
        set.remove(token);
    }
}
