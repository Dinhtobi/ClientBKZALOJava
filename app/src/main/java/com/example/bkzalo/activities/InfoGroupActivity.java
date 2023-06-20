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
import android.util.Base64;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.bkzalo.API.DetailGroupAPI;
import com.example.bkzalo.API.GroupAPI;
import com.example.bkzalo.API.ListGroupsAPI;
import com.example.bkzalo.API.ListMemberGroupAPI;
import com.example.bkzalo.R;
import com.example.bkzalo.adapters.ExpandableListViewAdapter;
import com.example.bkzalo.databinding.ActivityInfoGroupBinding;
import com.example.bkzalo.listeners.BottomsheetListener;
import com.example.bkzalo.models.DetailGroup;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.GroupItem;
import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.example.bkzalo.utilities.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoGroupActivity extends AppCompatActivity implements BottomsheetListener {

    private ActivityInfoGroupBinding binding;
    private PreferenceManager preferenceManager;
    private Map<GroupItem, List<UserModel>> listuser;
    private List<GroupItem> groups;
    private ExpandableListViewAdapter expandableListAdapter;
    private Group groupreceived;
    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        expandableListAdapter = new ExpandableListViewAdapter(this::BottomsheetClick);
        binding.imageBack.setEnabled(false);
        groupreceived = (Group) getIntent().getSerializableExtra(Constants.KEY_GROUP);
        LoadGroup();
        setListener();
    }
    private void LoadGroup(){
        byte[] bytes = android.util.Base64.decode(groupreceived.getImage(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
        encodedImage = encodedImage(bitmap);
        getListuser();
    }
    private void setListener(){
        binding.imageupdate.setOnClickListener(v->UpdateGroup());
        binding.imageclose.setOnClickListener(v->CloseClick());
        binding.imageback.setOnClickListener(v-> onBackPressed());
        binding.imageadd.setOnClickListener(v -> {
            if(isValidSignUpDetails()) {
                Update();
                CloseClick();
            }
        });
        binding.addmember.setOnClickListener(v->AddMember());
        binding.expandlistuser.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                    binding.addmember.setVisibility(View.VISIBLE);
            }
        });
        binding.expandlistuser.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                binding.addmember.setVisibility(View.GONE);
            }
        });
    }
    private void AddMember(){
        Intent intent = new Intent(getApplicationContext(), AddMemberActivity.class);
        groupreceived = (Group) getIntent().getSerializableExtra(Constants.KEY_GROUP);
        intent.putExtra(Constants.KEY_GROUP,  groupreceived);
        startActivity(intent);
    }
    private void UpdateGroup() {
        binding.imageadd.setVisibility(View.VISIBLE);
        binding.imageupdate.setVisibility(View.GONE);
        binding.imageback.setVisibility(View.GONE);
        binding.imageclose.setVisibility(View.VISIBLE);
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.Nickname.setEnabled(true);
        binding.layoutImage.setEnabled(true);

    }
    private void CloseClick(){
        binding.imageadd.setVisibility(View.GONE);
        binding.imageclose.setVisibility(View.GONE);
        binding.imageback.setVisibility(View.VISIBLE);
        binding.imageupdate.setVisibility(View.VISIBLE);
        binding.Nickname.setEnabled(false);
        binding.layoutImage.setEnabled(false);
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            encodedImage = encodedImage(bitmap);
                            groupreceived.setImage(encodedImage);
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private void Update(){
        Group group = new Group();
        group.setNamegroup(binding.inputName.getText().toString());
        group.setImage(encodedImage);
        group.setId(groupreceived.getId());
        group.setType("haveuser");
        GroupAPI.groupapi.updateGroup(group).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                showToast("Cập nhật thành công");
                groupreceived.setImage(group.getImage());
                groupreceived.setNamegroup(group.getNamegroup());
                LoadGroup();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
    private Boolean isValidSignUpDetails(){
        if(binding.inputName.getText().toString().trim().isEmpty()){
            showToast("Nhập Tên");
            return  false;
        }else{
            return true;
        }
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void getListuser(){
        Map<GroupItem,List<UserModel>> listMap = new HashMap<>();
        GroupItem group = new GroupItem();
        group.setId(1);
        group.setName("Thành viên nhóm");
        binding.inputName.setText(groupreceived.getNamegroup());
        List<UserModel> listuser = new ArrayList<>();
        ListMemberGroupAPI.listMemberGroupApi.listmember(groupreceived).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                try {
                    String jsonString = responseBody.string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    List<UserModel> list;
                    JsonElement messageElement = jsonObject.get("users");

                    if (messageElement != null && messageElement.isJsonArray()) {
                        JsonArray UserModelArray = messageElement.getAsJsonArray();
                        Type UserModelListType = new TypeToken<List<UserModel>>(){}.getType();
                        list = gson.fromJson(UserModelArray, UserModelListType);
                    } else {
                        list = new ArrayList<>(); // Hoặc giá trị mặc định tùy vào yêu cầu của bạn
                    }
                UserModel us = new UserModel();
                for(int i = 0 ; i<list.size(); i++){
                    UserModel newmember = new UserModel();
                    newmember.setName(list.get(i).getName());
                    newmember.setId(list.get(i).getId());
                    newmember.setUrl(list.get(i).getUrl());
                    newmember.setStatus(list.get(i).getStatus());
                    listuser.add(newmember);
                    if(String.valueOf(list.get(i).getId()).equals(preferenceManager.getString(Constants.KEY_USER_ID))){
                        us = list.get(i);
                    }
                }

                listMap.put(group,listuser);
                groups = new ArrayList<>(listMap.keySet());
                expandableListAdapter.setData(groups,listMap,us);
                binding.expandlistuser.setAdapter(expandableListAdapter);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private  String encodedImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    @Override
    public void BottomsheetClick(UserModel us) {
        View viewdialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet,null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewdialog);
        bottomSheetDialog.show();
        ListGroupsAPI.listGroupsApi.listgroup(us).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                try {
                    String jsonString = responseBody.string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    List<Group> list;
                    JsonElement messageElement = jsonObject.get("message");

                    if (messageElement != null && messageElement.isJsonArray()) {
                        JsonArray messagesArray = messageElement.getAsJsonArray();
                        Type messageListType = new TypeToken<List<Group>>(){}.getType();
                        list = gson.fromJson(messagesArray, messageListType);
                    } else {
                        list = new ArrayList<>(); // Hoặc giá trị mặc định tùy vào yêu cầu của bạn
                    }
                    boolean nguoitao = false;
                    for(Group i : list){
                        if(preferenceManager.getString(Constants.KEY_USER_ID).equals(String.valueOf(i.getId_createdbyuser()))){
                            nguoitao = true;
                            break;
                        }
                    }
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(String.valueOf(us.getId()))){
                        viewdialog.findViewById(R.id.btout).setVisibility(View.VISIBLE);
                        viewdialog.findViewById(R.id.btchat).setVisibility(View.GONE);
                    }else if(nguoitao){
                        viewdialog.findViewById(R.id.btroll).setVisibility(View.VISIBLE);
                        viewdialog.findViewById(R.id.btdel).setVisibility(View.VISIBLE);
                    }

                    viewdialog.findViewById(R.id.btdel).setOnClickListener(v->{
                        Del(us);
                        bottomSheetDialog.dismiss();
                    });
                    viewdialog.findViewById(R.id.btroll).setOnClickListener(v->{
                        ChangeAdmin(us);
                        bottomSheetDialog.dismiss();
                    });
                    boolean finalNguoitao = nguoitao;
                    viewdialog.findViewById(R.id.btout).setOnClickListener(v->{
                        if(finalNguoitao && expandableListAdapter.getChildrenCount(0) >1){
                            showToast("Chuyển quyền quản trị viên cho người khác");
                            bottomSheetDialog.dismiss();
                        }else{
                            Del(us);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    viewdialog.findViewById(R.id.btchat).setOnClickListener(v->Chat(us));
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
    public void Chat(UserModel us){
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USERMODEL,  us);
        startActivity(intent);
        finish();
    }
    public void ChangeAdmin(UserModel us){
        Group group = new Group() ;
        group.setId(groupreceived.getId());
        group.setId_createdbyuser(us.getId());
        group.setType("haveuser");
        GroupAPI.groupapi.updateGroup(group).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body() != null){
                    LoadGroup();
                    showToast("Success");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showToast("Lỗi hệ thống");
            }
        });
    }
    public void Del(UserModel us){
        DetailGroup detailGroup = new DetailGroup() ;
        detailGroup.setId_groupchat(groupreceived.getId());
        detailGroup.setId_user(us.getId());
        Date dnow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
        detailGroup.setTimeout(ft.format(dnow));
        detailGroup.setStatus(0);
        DetailGroupAPI.detailgroupapi.updateuseringroup(detailGroup).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                    LoadGroup();
                    showToast("Đã xoá");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showToast("Lỗi hệ thống");
            }
        });
    }
    protected void onResume() {
        super.onResume();
        LoadGroup();
    }
}