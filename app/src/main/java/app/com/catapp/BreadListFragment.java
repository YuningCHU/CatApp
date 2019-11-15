package app.com.catapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class BreadListFragment extends Fragment implements View.OnClickListener {

    private OnListFragmentInteractionListener mListener;
    private EditText tfKeyword;
    private Button btnSearch;
    private View searchView;

    private RecyclerView recyclerView;
    private BreedListAdapter adapter;
    private List<Breed> items = new ArrayList<Breed>();

    private Handler handler = new Handler();

    private boolean isSearch = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BreadListFragment() {

    }

    public void doSearch() {
        if(!isSearch) {
            searchView.setVisibility(View.VISIBLE);
            items.clear();
            adapter.notifyDataSetChanged();
            tfKeyword.setText("");
            isSearch = true;
        }
    }

    //show favorites
    public void doFavorite() {
        searchView.setVisibility(View.GONE);
        items.clear();
        items.addAll(FavoriteList.ITEMS);
        Log.e("fav", items.size()+"");
        adapter.notifyDataSetChanged();
        isSearch = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_breed_list, container, false);

        // Set the adapter

        Context context = view.getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //set divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new BreedListAdapter(items, mListener);
        recyclerView.setAdapter(adapter);

        tfKeyword = (EditText) view.findViewById(R.id.tfKeyword);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        searchView = view.findViewById(R.id.searchBar);

        btnSearch.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        items.clear();
        adapter.notifyDataSetChanged();

        //got data from web
        String keyword = tfKeyword.getText().toString().trim();

        String url = "https://api.thecatapi.com/v1/breeds/search?q=" + keyword;
        if (keyword.isEmpty()) {
            url = "https://api.thecatapi.com/v1/breeds";
        }

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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BreadListFragment.this.getContext(), "network error", Toast.LENGTH_SHORT).show();
                    }
                });
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

                        try {



                            Breed breed = new Breed();

                            breed.image = "";
                            breed.name = jsonObject.getString("name");
                            breed.id = jsonObject.getString("id");

                            breed.description = "none";
                            if(jsonObject.has("description")){
                                breed.description = jsonObject.getString("description");
                            }

                            breed.temperament = "none";
                            if(jsonObject.has("temperament")){
                                breed.temperament = jsonObject.getString("temperament");
                            }

                            breed.origin = "none";
                            if(jsonObject.has("origin")){
                                breed.origin = jsonObject.getString("origin");
                            }

                            breed.life_span = "none";
                            if(jsonObject.has("life_span")){
                                breed.life_span = jsonObject.getString("life_span");
                            }

                            breed.wikipedia_url = "none";
                            if(jsonObject.has("wikipedia_url")){
                                breed.wikipedia_url = jsonObject.getString("wikipedia_url");
                            }

                            breed.dog_friendly = "none";
                            if(jsonObject.has("dog_friendly")){
                                breed.dog_friendly = jsonObject.getInt("dog_friendly") + "";
                            }

                            breed.weightImperial = "none";
                            breed.weightMetric = "none";
                            if(jsonObject.has("weight")){
                                JSONObject weightObject = jsonObject.getJSONObject("weight");
                                breed.weightImperial = weightObject.getString("imperial");
                                breed.weightMetric = weightObject.getString("metric");
                            }

                            items.add(breed);
                        }catch (Exception e){
                            Log.e("json error",e.toString());
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //update ui
                            if(items.size() == 0){
                                Toast.makeText(BreadListFragment.this.getContext(), "no result", Toast.LENGTH_SHORT).show();
                            }else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("json error", e.getMessage());
                }
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Breed item);
    }
}
