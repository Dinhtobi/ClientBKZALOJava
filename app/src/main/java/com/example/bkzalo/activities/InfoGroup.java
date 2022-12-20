package com.example.bkzalo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.bkzalo.adapters.ExpandableListViewAdapter;
import com.example.bkzalo.databinding.ActivityInfoGroupBinding;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoGroup extends AppCompatActivity {

    private ActivityInfoGroupBinding binding;
    private PreferenceManager preferenceManager;
    private Map<Group, List<UserModel>> listuser;
    private List<Group> groups;
    private ExpandableListAdapter expandableListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        LoadUser();
    }
    private void LoadUser(){
        listuser = getListuser();
        groups = new ArrayList<>(listuser.keySet());
        expandableListAdapter = new ExpandableListViewAdapter(groups,listuser);
        binding.expandlistuser.setAdapter(expandableListAdapter);
    }
    private Map<Group , List<UserModel>> getListuser(){
        Map<Group,List<UserModel>> listMap = new HashMap<>();
        Group group = new Group();
        group.setId_nhomchat(1L);
        group.setTennhom("Thành viên nhóm");
        List<UserModel> list = new ArrayList<>();
        UserModel us = new UserModel();
        us.setId(1L);
        us.setTen("abc");
        UserModel user2 = new UserModel();
        user2.setId(2L);
        user2.setTen("test");
        UserModel user3 = new UserModel();
        user3.setId(3L);
        user3.setTen("1234");
       list.add(us);
        list.add(user2);
        list.add(user3);
        listMap.put(group,list);
        return listMap;
    }
}