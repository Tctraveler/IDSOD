package com.Tc_traveler.IDSOD.service;

import com.Tc_traveler.IDSOD.entity.User;

public interface UserService {
    User findUserByUsername(String username);

    void register(String username, String firstPassword);

    void addConsequence(Integer id, StringBuilder result);

}
