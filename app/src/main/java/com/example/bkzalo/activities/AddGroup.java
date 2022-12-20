package com.example.bkzalo.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;

import com.example.bkzalo.API.AddDetailGroupAPI;
import com.example.bkzalo.API.AddGroupAPI;
import com.example.bkzalo.API.GetUsersAPI;
import com.example.bkzalo.adapters.AddGroupAdapter;
import com.example.bkzalo.databinding.ActivityAddGroupBinding;
import com.example.bkzalo.listeners.CheckAddListener;
import com.example.bkzalo.listeners.SearchUserListener;
import com.example.bkzalo.models.DetailGroup;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.example.bkzalo.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroup extends AppCompatActivity implements CheckAddListener, SearchUserListener {
    private ActivityAddGroupBinding binding;
    private PreferenceManager preferenceManager;
    public String encodedImage;
    private AddGroupAdapter addGroupAdapter;

    private List<Long> detailGroups = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        SetListeners();
        addGroupAdapter = new AddGroupAdapter(this::onCheckClick);
        getUsers();
        SearchChangeListener();
    }

    private void SetListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.imageadd.setOnClickListener(v->AddGroup());
    }
    private  void AddGroup(){
        List<Long> list = detailGroups;
        Long id_nguoitao = Long.parseLong(preferenceManager.getString(Constants.KEY_USER_ID));
        list.add(id_nguoitao);
        Group group = new Group();
        group.setImage(encodedImage);
        group.setTennhom(binding.inputName.getText().toString());
        group.setId_nguoitao(id_nguoitao);
        AddGroupAPI.addgroupapi.addGroup(group).enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if(response.body()!=null){
                    Group newgroup = response.body();
                    Long id_nhomchat = newgroup.getId_nhomchat();
                   for(int i = 0 ; i < list.size(); i++){
                       DetailGroup newmember = new DetailGroup();
                       newmember.setId_nguoidung(list.get(i));
                       newmember.setId_nhomchat(id_nhomchat);
                       Date dnow = new Date();
                       SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd 'at' hh:mm:ss");
                       newmember.setThoigianthamgia(ft.format(dnow));
                      AddDetailGroupAPI.adddetailgroupapi.adduseringroup(newmember).enqueue(new Callback<DetailGroup>() {
                          @Override
                          public void onResponse(Call<DetailGroup> call, Response<DetailGroup> response) {
                              if(response.body()!=null){

                              }
                          }

                          @Override
                          public void onFailure(Call<DetailGroup> call, Throwable t) {
                            showErrorMessage();
                          }
                      });
                   }
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                    showErrorMessage();
            }
        });
        Intent intent = new Intent(getApplicationContext(), GroupListActivity.class);
        startActivity(intent);
    }
    private String encodedImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodedImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private List<UserModel> users = new ArrayList<>();

    private void getUsers() {
        loading(true);
        GetUsersAPI.getuserapi.GetList().enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                loading(false);
                List<UserModel> listus = response.body();
                String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                if (response.body() != null) {
                    for (int i = 0; i < listus.size(); i++) {
                        if (!currentUserId.equals(listus.get(i).getId().toString())) {
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
                        addGroupAdapter.setData(users);
                        binding.usersRecyclerView.setAdapter(addGroupAdapter);
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

        if(detailGroups.size()> 0){
            if(Checked(id_userModel)){
                detailGroups.add(id_userModel);
            }else{
                detailGroups.remove(id_userModel);
            }
        }else{
            detailGroups.add(id_userModel);
        }
    }
    private boolean Checked(Long id_userModel){
        boolean add = true;
        for(int i = 0 ; i<detailGroups.size(); i++){
            if(id_userModel.toString().equals(detailGroups.get(i).toString())){
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
                List<UserModel> list = addGroupAdapter.GetData();
                List<UserModel> listserach = new ArrayList<>();
                for(int i = 0 ; i< list.size() ; i++){
                    if(list.get(i).getTen().contains(search)){
                        listserach.add(list.get(i));
                    }
                }
                if(search.equals("")){
                    addGroupAdapter.setData(users);
                    binding.usersRecyclerView.setAdapter(addGroupAdapter);
                    binding.usersRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    addGroupAdapter.setData(listserach);
                    binding.usersRecyclerView.setAdapter(addGroupAdapter);
                    binding.usersRecyclerView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}