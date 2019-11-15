package app.com.catapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class DetailFragment extends Fragment implements View.OnClickListener, RatingBar.OnRatingBarChangeListener{

    private OnFragmentInteractionListener mListener;
    private ImageView image;
    private Breed breed;
    private Handler handler = new Handler();
    private TextView wikipedia_url;
    private RatingBar ratingBar;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        breed = (Breed)getActivity().getIntent().getSerializableExtra("breed");

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ratingBar = view.findViewById(R.id.fav);
        ratingBar.setMax(1);
        ratingBar.setStepSize(1);

        if(FavoriteList.ITEMS.contains(breed)){
            ratingBar.setRating(1);
        }else {
            ratingBar.setRating(0);
        }

        image = view.findViewById(R.id.image);
        TextView name = view.findViewById(R.id.name);
        name.setText(breed.name);
        TextView description = view.findViewById(R.id.description);
        description.setText(breed.description);
        TextView temperament = view.findViewById(R.id.temperament);
        temperament.setText("Temperament: " + breed.temperament);

        wikipedia_url = view.findViewById(R.id.wikipedia_url);
        wikipedia_url.setText("Wiki: " + breed.wikipedia_url);

        ((TextView)view.findViewById(R.id.origin)).setText("Origin: " + breed.origin);
        ((TextView)view.findViewById(R.id.life_span)).setText("Life Span: " + breed.life_span);
        ((TextView)view.findViewById(R.id.dog_friendly)).setText("Dog Friendly: " + breed.dog_friendly);
        ((TextView)view.findViewById(R.id.weightImperial)).setText("Imperial Weight: " + breed.weightImperial);
        ((TextView)view.findViewById(R.id.weightMetric)).setText("Metric Weight: " + breed.weightMetric);

        wikipedia_url.setOnClickListener(this);
        //ratingBar.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(this);

        downloadImageUrl();
        return view;
    }


    @Override
    public void onClick(View view) {
        Log.e("onClick",  "onClick");

        if(view == wikipedia_url) {
            onRequestWiki();
        }else if(view == ratingBar){
            /*
            float v = ratingBar.getRating();
            Log.e("rating", v + "");

            if(v == 0){
                FavoriteList.addItem(breed);
                ratingBar.setRating(1);
            }else{
                FavoriteList.removeItem(breed);
                ratingBar.setRating(0);
            }*/
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean fromUser) {
        if(fromUser){
            if(v == 0){
                FavoriteList.removeItem(breed);
            }else {
                FavoriteList.addItem(breed);
            }
        }
    }

    private void downloadImageUrl() {
        String url = "https://api.thecatapi.com/v1/images/search?breed_ids=" + breed.id;

        Log.e("search", url);

        //create okHttpClient object
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //create a Request
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", "caf74dbc-d7a4-4f02-875e-013d067e2967")
                .build();

        //new call
        Call call = mOkHttpClient.newCall(request);

        //submit request
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //network error
                Log.e("network", e.toString());
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                //got data form web

                String result = response.body().string();

                try {
                    //create a json array
                    JSONArray array = new JSONArray(result);

                    Log.e("count", array.length()+"");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        String url = jsonObject.getString("url");

                        breed.image = url;
                        downloadImage(url);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("json error", e.getMessage());
                }
            }
        });
    }

    public void downloadImage(String url){
        Log.e("search", url);

        //create okHttpClient object
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //create a Request
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", "caf74dbc-d7a4-4f02-875e-013d067e2967")
                .build();

        //new call
        Call call = mOkHttpClient.newCall(request);

        //submit request
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //network error
                Log.e("network", e.toString());
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                //got data form web

                ResponseBody body = response.body();
                InputStream in = body.byteStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(in);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        image.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    public void onRequestWiki() {
        if (mListener != null && !breed.wikipedia_url.equals("none")) {
            mListener.onFragmentInteraction(breed.wikipedia_url);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String wikiUrl);
    }

}
