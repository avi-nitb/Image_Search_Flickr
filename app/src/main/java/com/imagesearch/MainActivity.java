package com.imagesearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.imagesearch.adapter.ImageAdapter;
import com.imagesearch.models.SearchResultModel;
import com.imagesearch.networks.FlickerSearchApi;
import com.imagesearch.networks.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.imagesearch.utils.AppConstants.PAGE_NUM;
import static com.imagesearch.utils.AppConstants.SEARCH_TAG;

public class MainActivity extends AppCompatActivity {
    ImageView buttonSearch;
    Button buttonLoadMore, buttonPrevPage;
    EditText editTextSearch;
    TextView textViewLabel;
    RecyclerView rvImages;
    List<SearchResultModel.Photos.Photo> infoList = new ArrayList<>();
    List<String> urlList = new ArrayList<>();
    ImageAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Views
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonLoadMore = findViewById(R.id.buttonNextPage);
        editTextSearch = findViewById(R.id.editTextSearch);
        textViewLabel = findViewById(R.id.textViewLabel);
        rvImages = findViewById(R.id.rvImages);
        rvImages.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        adapter = new ImageAdapter(MainActivity.this, urlList);
        rvImages.setAdapter(adapter);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SEARCH_TAG = editTextSearch.getText().toString().trim();
                PAGE_NUM = 1;
                textViewLabel.setVisibility(View.GONE);
                getData();

                }
        });

        buttonLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PAGE_NUM = PAGE_NUM + 1;
                getData();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                urlList.clear();
            }
        });
    }

    void getData(){

        FlickerSearchApi instance = RetrofitClientInstance.getRetrofitInstance().create(FlickerSearchApi.class);
        Call<JsonElement> call =instance.getSearchResult( "flickr.photos.search","3189212285dcb4cf5b2f044edcb0544e",
                SEARCH_TAG, "json",50, PAGE_NUM);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                try {
                    SearchResultModel data = new Gson().fromJson(response.body(), SearchResultModel.class);
                    Log.d("response", response.body() +"");
                    infoList.clear(); // Clear the list to get new data
                    infoList.addAll(data.getPhotos().getPhoto());

                    for (int i = 0; i < infoList.size(); i++) {
                        String url = "https://farm" + infoList.get(i).getFarm() + ".staticflickr.com/"
                                + infoList.get(i).getServer() + "/"
                                + infoList.get(i).getId() + "_" + infoList.get(i).getSecret() + ".jpg";
                        urlList.add(url);
                    }
                    //If it is first call then set Adapter otherwise just notify adapter that next page data is inserted in list
                    if (data.getPhotos().getPage() > 1) {
                        adapter.notifyItemRangeInserted(((data.getPhotos().getPage()) * (data.getPhotos().getPerpage())) -1,
                                data.getPhotos().getPerpage());
                    } else {
                        rvImages.setAdapter(adapter);
                    }
                } catch (Exception exception){
                    Log.d("Exception Caught", exception.toString());
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.d("Exception", t.toString());
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey("Url_List")){
            rvImages.setAdapter(new ImageAdapter(MainActivity.this, savedInstanceState.getStringArrayList("Url_List")));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putStringArrayList("Url_List", (ArrayList<String>) urlList);
        super.onSaveInstanceState(outState);
    }

}
