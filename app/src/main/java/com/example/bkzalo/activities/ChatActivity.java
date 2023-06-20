package com.example.bkzalo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bkzalo.API.BoxMessageAPI;
import com.example.bkzalo.API.GetUsersAPI;
import com.example.bkzalo.API.ListBoxMessageAPI;
import com.example.bkzalo.API.ListMessageAPI;
import com.example.bkzalo.API.MessageAPI;
import com.example.bkzalo.API.SetOnlineAPI;
import com.example.bkzalo.adapters.ChatAdapter;
import com.example.bkzalo.databinding.ActivityChatBinding;
import com.example.bkzalo.models.BoxLastMessage;
import com.example.bkzalo.models.Message;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity  {
    private ActivityChatBinding binding;
    private UserModel receiverUser;
    private List<Message> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private String conversionId = null;
    private int sizesend  ;
    private  int sizereceid;
    private  Timer timer;
    private TimerTask task ;
    private String encodedImage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
        Reload();
    }
    private void Reload(){
         timer = new Timer();
         task = new TimerTask() {
            @Override
            public void run() {
                listenAvailabilityOfReceiver();
                listenMessage();
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }
    // khởi tạo ban đầu
    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                preferenceManager.getString(Constants.KEY_USER_ID),
                getBitmapFromEncodedString(receiverUser.getUrl())
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
    }
    // gửi tin nhắn
    private void sendMessage(int fileformat) {
        Message mes = new Message();
        mes.setId_sender(Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID)));
        mes.setId_receiver(receiverUser.getId());
        Date dnow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd 'at' hh:mm:ss");
        mes.setContent(binding.inputMessage.getText().toString());
        mes.setCreateAt(ft.format(dnow));
        mes.setFileformat(fileformat);
        MessageAPI.messageAPI.SendChat(mes).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                showToast("Fail");
            }
        });
        if (conversionId != null) {
            BoxLastMessage box = new BoxLastMessage() ;
            box.setId(Integer.parseInt(conversionId));
            if(fileformat == 1){
                box.setLastmessage( preferenceManager.getString(Constants.KEY_NAME)+" Đã gửi 1 ảnh");
            }else{
                box.setLastmessage(binding.inputMessage.getText().toString());
            }
            box.setCreateAt(ft.format(dnow));
            updateConversion(box);
        }else {
            BoxLastMessage box = new BoxLastMessage();
            box.setId_sender(Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID)));
            box.setNamesender(preferenceManager.getString(Constants.KEY_NAME));
            box.setUrlsender(preferenceManager.getString(Constants.KEY_IMAGE));
            box.setId_receiver(receiverUser.getId());
            box.setNamreceiver(receiverUser.getName());
            box.setUrlreceiver(receiverUser.getUrl());
            if(fileformat == 1){
                box.setLastmessage(preferenceManager.getString(Constants.KEY_NAME)+" Đã gửi 1 ảnh");
            }else{
                box.setLastmessage(binding.inputMessage.getText().toString());
            }
            box.setCreateAt(ft.format(dnow));
            box.setType("Add");
            addConversion(box);
        }
        binding.inputMessage.setText(null);
    }
    // kiểm tra tình trạng người sử dụng, đang hoạt động hay không.
    private void listenAvailabilityOfReceiver() {
        GetUsersAPI.getuserapi.GetList("All").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                try{
                    String jsonString = responseBody.string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    JsonArray usersArray = jsonObject.getAsJsonArray("users");
                    Type userListType = new TypeToken<List<UserModel>>(){}.getType();
                    List<UserModel> userList = gson.fromJson(usersArray, userListType);
                    if(response.body() != null){
                        if(CheckOnl(userList)){
                            binding.textAvailability.setVisibility(View.VISIBLE);
                        }
                        else {
                            binding.textAvailability.setVisibility(View.GONE);
                        }
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

    // kiệm tra online
    private boolean CheckOnl(List<UserModel> list){
        boolean onl = false;
        for(UserModel i : list){
            if(i.getId() == receiverUser.getId() && i.getStatus() == 1 ){
                onl = true;
                break;
            }
        }
        return  onl;
    }
    // Lấy tin nhắn cũ
    private void listenMessage() {
        Message mes = new Message();
        mes.setId_sender(Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID)));
        mes.setId_receiver(receiverUser.getId());
        ListMessageAPI.listmessageapi.ListMes(mes).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                try {
                    String jsonString = responseBody.string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    JsonArray messagesArray = jsonObject.getAsJsonArray("message");
                    Type messageListType = new TypeToken<List<Message>>(){}.getType();
                    List<Message> listchat = gson.fromJson(messagesArray, messageListType);
                    sizesend = listchat.size();
                    eventListener(listchat);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Fail");
            }
        });
        Message mes2 = new Message();
        mes2.setId_sender(receiverUser.getId());
        mes2.setId_receiver(Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID)));
        ListMessageAPI.listmessageapi.ListMes(mes2).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                try {
                    String jsonString = responseBody.string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    JsonArray messagesArray = jsonObject.getAsJsonArray("message");
                    Type messageListType = new TypeToken<List<Message>>(){}.getType();
                    List<Message> listchat = gson.fromJson(messagesArray, messageListType);
                    sizereceid = listchat.size();
                    eventListener(listchat);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Fail");
            }
        });
    }
    private  void eventListener(List<Message> chat) {
        int count = chatMessages.size();
        int size = sizesend + sizereceid;
        if(chatMessages.size() == size){

        }else {
            if (chat != null) {
                for (Message i : chat) {
                    Message chatMessage = new Message();
                    chatMessage.setId(i.getId());
                    chatMessage.setId_sender(i.getId_sender());
                    chatMessage.setId_receiver(i.getId_receiver());
                    chatMessage.setContent(i.getContent());
                    chatMessage.setCreateAt(i.getCreateAt());
                    chatMessage.setFileformat(i.getFileformat());
                    if(CheckMes(chatMessage)){
                        chatMessages.add(chatMessage);
                    }
                }
                Collections.sort(chatMessages, (obj1, obj2) -> obj1.getCreateAt().compareTo(obj2.getCreateAt()));
                if (count == 0) {
                    chatAdapter.notifyDataSetChanged();
                } else {
                    chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                    binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                }
                binding.chatRecyclerView.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
            if (conversionId == null) {
                checkForConversion();
            }
        }
    }
    // Chuyển String sang ảnh
    private Bitmap getBitmapFromEncodedString (String encodedImage) {
        byte[] bytes = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    // Load hồ sơ người nhận
    private void loadReceiverDetails() {
        receiverUser = (UserModel) getIntent().getSerializableExtra(Constants.KEY_USERMODEL);
        binding.textName.setText(receiverUser.getName());
    }
    // Set hoạt động cho giao diện
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage(0));
        binding.upload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            timer.cancel();
            task.cancel();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
    // Thêm hộp chat
    private void addConversion(BoxLastMessage conversion) {
        BoxMessageAPI.boxmessageAPI.converBox(conversion).enqueue(new Callback<BoxLastMessage>() {
            @Override
            public void onResponse(Call<BoxLastMessage> call, Response<BoxLastMessage> response) {
                showToast("Success!");
            }

            @Override
            public void onFailure(Call<BoxLastMessage> call, Throwable t) {
                showToast("Lỗi dữ liệu!");
            }
        });
    }
    //Cập nhật hộp chat
    private void updateConversion(BoxLastMessage boxLastMessage) {
        BoxMessageAPI.boxmessageAPI.Update(boxLastMessage).enqueue(new Callback<BoxLastMessage>() {
            @Override
            public void onResponse(Call<BoxLastMessage> call, Response<BoxLastMessage> response) {
                showToast("Success!");
            }

            @Override
            public void onFailure(Call<BoxLastMessage> call, Throwable t) {
                showToast("Lỗi dữ liệu!");
            }
        });
    }
    // kiểm tra sự tồn tại hộp chat
    private void  checkForConversion() {
        if (chatMessages.size() != 0) {
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    String.valueOf(receiverUser.getId())
            );
            checkForConversionRemotely(
                    String.valueOf(receiverUser.getId()),
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderId, String receiverId) {
       BoxLastMessage last = new BoxLastMessage();
       last.setId_sender(Integer.parseInt(senderId));
       last.setId_receiver(Integer.parseInt(receiverId));
       last.setType("Check");
        ListBoxMessageAPI.listboxmessageAPI.ListBOX(last).enqueue(new Callback<ResponseBody>() {
           @Override
           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               if(response.body() != null){
                   ResponseBody responseBody = response.body();
                   try {
                       String jsonString = responseBody.string();
                       Gson gson = new GsonBuilder().create();
                       JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                       BoxLastMessage box = gson.fromJson(jsonObject.getAsJsonObject("message"), BoxLastMessage.class);
                       conversion(box);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }

           }

           @Override
           public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Lỗi tải dữ liệu!");
           }
       });
    }
    // kiểm tra và Gán id hộp chat
    private final void  conversion(BoxLastMessage boxLastMessage){
        if (boxLastMessage != null) {
            BoxLastMessage box = boxLastMessage;
            if(box.getId() != 0){
            conversionId = String.valueOf(box.getId());
        }}
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
        private void showToast(String message){
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    private boolean CheckMes(Message chat){
            boolean add = true ;
            for (int i = 0 ; i < chatMessages.size() ; i++){
                if(chatMessages.get(i).getId() == chat.getId() ){
                    add = false ;
                    break;
                }
            }
            return add;
    }
    // chuyển ảnh sang String
    private  String encodedImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    // Chọn ảnh trong thư viện
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            encodedImage = encodedImage(bitmap);
                            binding.inputMessage.setText(encodedImage);
                            sendMessage(1);
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}