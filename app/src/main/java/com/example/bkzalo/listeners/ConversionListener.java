package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;

public interface ConversionListener {
    void onConversionUserClicked(UserModel user);
    void onConversionGroupClicked(Group group);
}
