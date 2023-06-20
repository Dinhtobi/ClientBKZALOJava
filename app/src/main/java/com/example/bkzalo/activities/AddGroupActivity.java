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

import com.example.bkzalo.API.DetailGroupAPI;
import com.example.bkzalo.API.GroupAPI;
import com.example.bkzalo.API.GetUsersAPI;
import com.example.bkzalo.adapters.AddMemberAdapter;
import com.example.bkzalo.databinding.ActivityAddGroupBinding;
import com.example.bkzalo.listeners.CheckAddListener;
import com.example.bkzalo.listeners.SearchUserListener;
import com.example.bkzalo.models.DetailGroup;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.example.bkzalo.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroupActivity extends AppCompatActivity implements CheckAddListener, SearchUserListener {
    private ActivityAddGroupBinding binding;
    private PreferenceManager preferenceManager;
    public String encodedImage;
    private AddMemberAdapter addMemberAdapter;

    private List<Integer> detailGroups = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        SetListeners();
        addMemberAdapter = new AddMemberAdapter(this::onCheckClick);
        getUsers();
        SearchChangeListener();
    }
    // Set hành động cho giao diện
    private void SetListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.imageadd.setOnClickListener(v->AddGroup());
    }
    // Thêm group
    private  void AddGroup(){
        List<Integer> list = detailGroups;
        int id_nguoitao = Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID));
        list.add(id_nguoitao);
        Group group = new Group();
        group.setImage(encodedImage);
        group.setNamegroup(binding.inputName.getText().toString());
        group.setId_createdbyuser(id_nguoitao);
        GroupAPI.groupapi.addGroup(group).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    ResponseBody responseBody = response.body();
                    try {
                        String jsonString = responseBody.string();
                        Gson gson = new GsonBuilder().create();
                        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                        Group newgroup = gson.fromJson(jsonObject.getAsJsonObject("message"), Group.class);
                    int id_nhomchat = newgroup.getId();
                   for(int i = 0 ; i < list.size(); i++){
                       DetailGroup newmember = new DetailGroup();
                       newmember.setId_user(list.get(i));
                       newmember.setId_groupchat(id_nhomchat);
                       Date dnow = new Date();
                       SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd 'at' hh:mm:ss");
                       newmember.setTimejoin(ft.format(dnow));
                      DetailGroupAPI.detailgroupapi.adduseringroup(newmember).enqueue(new Callback<DetailGroup>() {
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showErrorMessage();
            }
        });
        Intent intent = new Intent(getApplicationContext(), GroupListActivity.class);
        startActivity(intent);
    }
    // chuyển ảnh sang String
    private String encodedImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    // Chọn ảnh trong thư viện máy
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
    // Lấy danh sách người dùng
    private void getUsers() {
        loading(true);
        GetUsersAPI.getuserapi.GetList("All").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loading(false);
                ResponseBody responseBody = response.body();
                try{
                    String jsonString = responseBody.string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    JsonArray usersArray = jsonObject.getAsJsonArray("users");
                    Type userListType = new TypeToken<List<UserModel>>(){}.getType();
                    List<UserModel> listus = gson.fromJson(usersArray, userListType);
                String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                if (response.body() != null) {
                    for (int i = 0; i < listus.size(); i++) {
                        if (!currentUserId.equals(String.valueOf(listus.get(i).getId()))) {
                            UserModel user = new UserModel();
                            user.setName(listus.get(i).getName());
                            user.setEmail(listus.get(i).getEmail());
                            user.setUrl(listus.get(i).getUrl());
                            user.setStatus(listus.get(i).getStatus());
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
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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

    //xứ lý checkboxclick
    @Override
    public void onCheckClick(int id_userModel) {

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
    // kiểm tra checkbox
    private boolean Checked(int id_userModel){
        boolean add = true;
        for(int i = 0 ; i<detailGroups.size(); i++){
            if(id_userModel == detailGroups.get(i)){
               add= false;
               break;
            }
        }
        return add;
    }
    // tìm kiếm
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
                    if(list.get(i).getName().contains(search)){
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