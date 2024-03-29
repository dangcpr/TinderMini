package com.example.tinderforit;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Parameters
    StorageReference storageReference;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://tinder-it-e576d-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth;

    private RecyclerView chattingRecyclerView;
    private final List<ChatList> chatLists=new ArrayList<ChatList>();
    private ChatAdapter chatAdapter;

    private String getChatKey;
    private String getUID;

    private Button butGetCall;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri imageUri;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {     super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat, container, false);

        final ImageView btn_arrowBack=view.findViewById(R.id.btn_arrowBack);
        final TextView txt_username=view.findViewById(R.id.txt_username);
        final EditText edt_messagesEditText=view.findViewById(R.id.edt_messagesEditText);
        final ImageView btn_sendPic = view.findViewById(R.id.btn_sendPic);
        final ImageView btn_send=view.findViewById(R.id.btn_send);
        final CircleImageView ptr_userProfile=view.findViewById(R.id.ptr_userProfile);
        //firebase authentication
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
        final String getUIDCurrentUser=mAuth.getUid();
        //get data from message adapter class
        Bundle bundle=this.getArguments();
        final  String getUserName= bundle.getString("userName");
        final  String getUserProfile= bundle.getString("userProfileURL");
        getChatKey= bundle.getString("userChatKey");
        getUID= bundle.getString("userUID");


        txt_username.setText(getUserName);
        Picasso.get().load(getUserProfile).into(ptr_userProfile);
        //Recycler view
        chattingRecyclerView=view.findViewById(R.id.list_messages);
        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatAdapter=new ChatAdapter(chatLists,getActivity());
        chattingRecyclerView.setAdapter(chatAdapter);
        //set last time seen msg
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int countMsg =(int)snapshot.child("Chat").child(getChatKey).child("Messages").getChildrenCount();
                databaseReference.child("UserProfile").child(getUIDCurrentUser).child("Connection").child("Match").child(getUID).setValue(countMsg);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //set adapter msg
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getChatKey.isEmpty()) {
                    getChatKey = String.valueOf(snapshot.child("Chat").getChildrenCount()+1);
                }
                chatLists.clear();
                if (snapshot.hasChild("Chat")) {
                    final String getFirstUID= snapshot.child("Chat").child(getChatKey).child("FirstUID").getValue(String.class);
                    final String getSecondUID= snapshot.child("Chat").child(getChatKey).child("SecondUID").getValue(String.class);
                    if (snapshot.child("Chat").child(getChatKey).hasChild("Messages")){
                        chatLists.clear();

                        if((getFirstUID.equals(getUID) && getSecondUID.equals(getUIDCurrentUser)) || (getSecondUID.equals(getUID) && getFirstUID.equals(getUIDCurrentUser))) {
                            for (DataSnapshot messagesSnapshot : snapshot.child("Chat").child(getChatKey).child("Messages").getChildren()) {
                                if (messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("FromUID")) {

                                    final String messagesTimestamp = messagesSnapshot.getKey();
                                    final String messagesFromUID = messagesSnapshot.child("FromUID").getValue(String.class);
                                    final String messagesText = messagesSnapshot.child("msg").getValue(String.class);
                                    final String messagesType = messagesSnapshot.child("type").getValue(String.class);

                                    //assert messagesTimestamp != null;
                                    Timestamp timestamp = new Timestamp(Long.parseLong(messagesTimestamp));
                                    Date date=new Date(timestamp.getTime());
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
                                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa",Locale.getDefault());

                                    //Add new msg to RecyclerView
                                    ChatList chatList = new ChatList(messagesFromUID, messagesText, simpleDateFormat.format(date), simpleTimeFormat.format(date), messagesType);
                                    chatLists.add(chatList);
                                    chatAdapter.updateChatList(chatLists);
                                    chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessagesFragment messagesFragment=new MessagesFragment();
                FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.card_frame,messagesFragment).commit();

            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getChatMessage=edt_messagesEditText.getText().toString();
                if(!getChatMessage.equals("")){
                    final String currentTimestamp=String.valueOf(System.currentTimeMillis());
                    Timestamp timestamp = new Timestamp(Long.parseLong(currentTimestamp));
                    Date date=new Date(timestamp.getTime());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa",Locale.getDefault());
                    final String messagesFromUID=mAuth.getUid();
                    final String messagesToUID=getUID;

                    databaseReference.child("Chat").child(getChatKey).child("FirstUID").setValue(messagesFromUID);
                    databaseReference.child("Chat").child(getChatKey).child("SecondUID").setValue(messagesToUID);
                    databaseReference.child("Chat").child(getChatKey).child("Messages").child(currentTimestamp).child("FromUID").setValue(mAuth.getUid());
                    databaseReference.child("Chat").child(getChatKey).child("Messages").child(currentTimestamp).child("msg").setValue(getChatMessage);
                    databaseReference.child("Chat").child(getChatKey).child("Messages").child(currentTimestamp).child("type").setValue("text");


                    //clear buff Edit text
                    edt_messagesEditText.setText("");
                }
            }
        });

        btn_sendPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });
        //Phím thực hiện cuộc gọi
        butGetCall = (Button) view.findViewById(R.id.butGetCall);
        butGetCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getBaseContext(), CallActivity.class);
                i.putExtra("userUID_Call", getUID);
                getActivity().startActivity(i);
            }
        });
        return view;
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void choosePhotoFromGallary()
    {
        mGetContent.launch("image/*");
    }

    private void takePhotoFromCamera()
    {
        if(ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA},1);
            return;
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(intent);
        }
    }

    private ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null){
                        imageUri = result;
                        uploadImage();
                    }
                }
            });

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == -1 && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    // code này để chuyển bitmap thành link Uri
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "val", null);
                    Uri uri = Uri.parse(path);
                    imageUri = uri;
                    uploadImage();
                }
            }
    );

    private void uploadImage() {
        if (imageUri != null){
            UUID uuid = UUID.randomUUID();
            StorageReference reference = storage.getReference().child("Image/" + uuid);

            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("imageUrl", String.valueOf(uri));

                                    final String getChatMessage= String.valueOf(uri);
                                    if(!getChatMessage.equals("")){
                                        final String currentTimestamp=String.valueOf(System.currentTimeMillis());
                                        Timestamp timestamp = new Timestamp(Long.parseLong(currentTimestamp));
                                        Date date=new Date(timestamp.getTime());
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
                                        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa",Locale.getDefault());
                                        final String messagesFromUID=mAuth.getUid();
                                        final String messagesToUID=getUID;

                                        databaseReference.child("Chat").child(getChatKey).child("FirstUID").setValue(messagesFromUID);
                                        databaseReference.child("Chat").child(getChatKey).child("SecondUID").setValue(messagesToUID);
                                        databaseReference.child("Chat").child(getChatKey).child("Messages").child(currentTimestamp).child("FromUID").setValue(mAuth.getUid());
                                        databaseReference.child("Chat").child(getChatKey).child("Messages").child(currentTimestamp).child("msg").setValue(getChatMessage);
                                        databaseReference.child("Chat").child(getChatKey).child("Messages").child(currentTimestamp).child("type").setValue("pic");

                                        //update messages
                                        ChatList chatList = new ChatList(messagesFromUID, getChatMessage,simpleDateFormat.format(date), simpleTimeFormat.format(date), "pic");
                                        chatLists.add(chatList);
                                        chatAdapter.updateChatList(chatLists);
                                        chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
                                        //update last time users seen msg
                                        //set last time seen msg
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int countMsg =(int)snapshot.child("Chat").child(getChatKey).child("Messages").getChildrenCount();
                                                databaseReference.child("UserProfile").child(messagesFromUID).child("Connection").child("Match").child(getUID).setValue(countMsg);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Add image failure", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

class ChatList {
    private String FromUID, Msg, DateSend, TimeSend, Type;

    public ChatList(String fromUID, String msg, String dateSend, String timeSend, String type) {
        FromUID = fromUID;
        Msg = msg;
        DateSend = dateSend;
        TimeSend = timeSend;
        Type = type;
    }

    public String getFromUID() {
        return FromUID;
    }

    public String getMsg() {
        return Msg;
    }

    public String getDateSend() {
        return DateSend;
    }

    public String getTimeSend() {
        return TimeSend;
    }

    public String getType(){
        return Type;
    }
}