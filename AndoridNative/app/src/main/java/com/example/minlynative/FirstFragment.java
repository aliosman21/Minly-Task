package com.example.minlynative;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;;
import com.example.minlynative.databinding.FragmentFirstBinding;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    Socket socket ;
    private String mImageUrl = "";
    private ViewGroup cont;
    private static final int INTENT_REQUEST_CODE = 100;

    public static final String URL = "http://10.0.2.2:3000";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
//        Log.d("View Created", "I AM HERE");
        cont = container;
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void getNewPhotos (){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<List<String>> repos = retrofitInterface.getImages();
        repos.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, retrofit2.Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> responseBody = response.body();
                    Log.d("Response", "ON response " + responseBody);
                    addImagesToLayout(responseBody);
                } else {

                    ResponseBody errorBody = response.errorBody();
                    Log.d("ERROR", String.valueOf(errorBody));


                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d("Failure Tag", "onFailure: "+t.getLocalizedMessage());
            }
        });
    }

    public void addImagesToLayout(List<String> urls){
        Collections.reverse(urls);
        adaptor adp = new adaptor(urls);
        ((RecyclerView) getActivity().findViewById(R.id.RView)).setAdapter(adp);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IO.Options options = IO.Options.builder().build();
        try {
            socket = IO.socket(URL, options);
            socket.connect();
            socket.on("message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
//                    Log.d("TAG", "call: CALLED");
                    getNewPhotos();

                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        getNewPhotos();
        binding.Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 100);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("RESULT", "onActivityResult: RESULT");
        if (requestCode == INTENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                    Log.d("TRYING", "WE TRYING LADS");
                    uploadImage(getBytes(is));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public byte[] getBytes(InputStream is) throws IOException {
        Log.d("BYTES", "Getting bytes");

        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }

    private void uploadImage(byte[] imageBytes) {
//        Log.d("UPLOADING", "uploadImage: TRYING TO UPLOAD");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", "image.jpg", requestFile);
        Call<Response> call = retrofitInterface.uploadImage(body);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Response responseBody = response.body();
                    mImageUrl = URL + responseBody.getPath();
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Log.d("ERROR", String.valueOf(errorBody));

                }
                getNewPhotos();
            }
            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d("Failure Tag", "onFailure: "+t.getLocalizedMessage());
            }
        });

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

