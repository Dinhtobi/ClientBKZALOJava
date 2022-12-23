package com.example.bkzalo.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.attribute.GroupPrincipal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoGroup extends AppCompatActivity implements BottomsheetListener {

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
        LoadGroup();
        setListener();
    }
    private void LoadGroup(){
        groupreceived = (Group) getIntent().getSerializableExtra(Constants.KEY_GROUP);
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
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private void Update(){
        Group group = new Group();
        group.setTennhom(binding.inputName.getText().toString());
        group.setImage(encodedImage);
        group.setId_nhomchat(groupreceived.getId_nhomchat());
        group.setType("haveuser");
        GroupAPI.groupapi.updateGroup(group).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                showToast("Cập nhật thành công");
                groupreceived.setImage(group.getImage());
                groupreceived.setTennhom(group.getTennhom());
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
        binding.inputName.setText(groupreceived.getTennhom());
        List<UserModel> listuser = new ArrayList<>();
        ListMemberGroupAPI.listMemberGroupApi.listmember(groupreceived).enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                List<UserModel> list = response.body();
                UserModel us = new UserModel();
                for(int i = 0 ; i<list.size(); i++){
                    UserModel newmember = new UserModel();
                    newmember.setTen(list.get(i).getTen());
                    newmember.setId(list.get(i).getId());
                    newmember.setUrl(list.get(i).getUrl());
                    newmember.setTrangthai(list.get(i).getTrangthai());
                    listuser.add(newmember);
                    if(list.get(i).getId().toString().equals(preferenceManager.getString(Constants.KEY_USER_ID))){
                        us = list.get(i);
                    }
                }

                listMap.put(group,listuser);
                groups = new ArrayList<>(listMap.keySet());
                expandableListAdapter.setData(groups,listMap,us);
                binding.expandlistuser.setAdapter(expandableListAdapter);
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {

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
        ListGroupsAPI.listGroupsApi.listgroup(us).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                List<Group> list = response.body();
                boolean nguoitao = false;
                for(Group i : list){
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(i.getId_nguoitao().toString())){
                        nguoitao = true;
                        break;
                    }
                }
                if(preferenceManager.getString(Constants.KEY_USER_ID).equals(us.getId().toString())){
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
                    if(finalNguoitao){
                        showToast("Chuyển quyền quản trị viên cho người khác");
                        bottomSheetDialog.dismiss();
                    }else{
                        Del(us);
                        Intent intent = new Intent(getApplicationContext(), GroupListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                viewdialog.findViewById(R.id.btchat).setOnClickListener(v->Chat(us));
            }
            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {

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
        group.setId_nhomchat(groupreceived.getId_nhomchat());
        group.setId_nguoitao(us.getId());
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
        detailGroup.setId_nhomchat(groupreceived.getId_nhomchat());
        detailGroup.setId_nguoidung(us.getId());
        Date dnow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
        detailGroup.setThoigianroikhoi(ft.format(dnow));
        detailGroup.setTrangthai(0);
        DetailGroupAPI.detailgroupapi.updateuseringroup(detailGroup).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body() != null){
                    LoadGroup();
                    showToast("Đã xoá");
                }
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