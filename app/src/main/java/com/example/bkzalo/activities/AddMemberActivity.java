package com.example.bkzalo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.bkzalo.API.DetailGroupAPI;
import com.example.bkzalo.API.GetUsersAPI;
import com.example.bkzalo.API.ListMemberGroupAPI;
import com.example.bkzalo.adapters.AddMemberAdapter;
import com.example.bkzalo.databinding.ActivityAddMemberBinding;
import com.example.bkzalo.listeners.CheckAddListener;
import com.example.bkzalo.listeners.SearchUserListener;
import com.example.bkzalo.models.DetailGroup;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.example.bkzalo.utilities.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemberActivity extends AppCompatActivity implements CheckAddListener, SearchUserListener {
    private ActivityAddMemberBinding binding;
    private PreferenceManager preferenceManager;
    private AddMemberAdapter addMemberAdapter;
    private List<Long> IdMember = new ArrayList<>();
    private Group groupreceived;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        groupreceived = (Group) getIntent().getSerializableExtra(Constants.KEY_GROUP);
        SetListeners();
        addMemberAdapter = new AddMemberAdapter(this::onCheckClick);
        getUsers();
        SearchChangeListener();
    }
    private void SetListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.imageadd.setOnClickListener(v->{
            AddMember();
            onBackPressed();
        });
    }
    private  void AddMember(){
        List<Long> listnewmember = IdMember;
        Long id_nhomchat = groupreceived.getId_nhomchat();
        for(int i =0 ; i<listnewmember.size(); i++){
            DetailGroup newmember = new DetailGroup();
            newmember.setId_nhomchat(id_nhomchat);
            newmember.setId_nguoidung(listnewmember.get(i));
            Date dnow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
            newmember.setThoigianthamgia(ft.format(dnow));
            newmember.setTrangthai(1);
            DetailGroupAPI.detailgroupapi.adduseringroup(newmember).enqueue(new Callback<DetailGroup>() {
                @Override
                public void onResponse(Call<DetailGroup> call, Response<DetailGroup> response) {
                    if(response.body() == null){
                        showErrorMessage();
                    }
                }

                @Override
                public void onFailure(Call<DetailGroup> call, Throwable t) {
                    showErrorMessage();
                }
            });
        }

    }
    private List<UserModel> users = new ArrayList<>();

    private void getUsers() {
        loading(true);
        ListMemberGroupAPI.listMemberGroupApi.listmember(groupreceived).enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                List<UserModel> useringroup = response.body();
                GetUsersAPI.getuserapi.GetList().enqueue(new Callback<List<UserModel>>() {
                    @Override
                    public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                        loading(false);
                        List<UserModel> listus = response.body();
                        if (response.body() != null) {
                            for (int i = 0; i < listus.size(); i++) {
                                if (Checkmember(listus.get(i),useringroup)) {
                                    UserModel user = new UserModel();
                                    user.setTen(listus.get(i).getTen());
                                    user.setEmail(listus.get(i).getEmail());
                                    user.setUrl(listus.get(i).getUrl());
                                    user.setTrangthai(listus.get(i).getTrangthai());
                                    user.setId(listus.get(i).getId());
                                    users.add(user);
                                }
                            }
                            if (users.size() > 0) {
                                addMemberAdapter.setData(users);
                                binding.usersRecyclerView.setAdapter(addMemberAdapter);
                                binding.usersRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                showErrorMessage();
                            }
                        } else {
                            showErrorMessage();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserModel>> call, Throwable t) {

                    }

                });
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {

            }
        });
    }
    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "Không có người nào cả"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public void onCheckClick(Long id_userModel) {

        if(IdMember.size()> 0){
            if(Checked(id_userModel)){
                IdMember.add(id_userModel);
            }else{
                IdMember.remove(id_userModel);
            }
        }else{
            IdMember.add(id_userModel);
        }
    }
    private boolean Checkmember(UserModel user , List<UserModel> listuser){
        boolean Added = true ;
        for(int i = 0 ; i<listuser.size(); i++){
            if(user.getId().equals(listuser.get(i).getId())){
                Added = false;
                break;
            }
        }
        return Added;
    }
    private boolean Checked(Long id_userModel){
        boolean add = true;
        for(int i = 0 ; i<IdMember.size(); i++){
            if(id_userModel.toString().equals(IdMember.get(i).toString())){
                add= false;
                break;
            }
        }
        return add;
    }

    @Override
    public void SearchChangeListener() {
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = binding.inputSearch.getText().toString();
                List<UserModel> list = addMemberAdapter.GetData();
                List<UserModel> listserach = new ArrayList<>();
                for(int i = 0 ; i< list.size() ; i++){
                    if(list.get(i).getTen().contains(search)){
                        listserach.add(list.get(i));
                    }
                }
                if(search.equals("")){
                    addMemberAdapter.setData(users);
                    binding.usersRecyclerView.setAdapter(addMemberAdapter);
                    binding.usersRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    addMemberAdapter.setData(listserach);
                    binding.usersRecyclerView.setAdapter(addMemberAdapter);
                    binding.usersRecyclerView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}