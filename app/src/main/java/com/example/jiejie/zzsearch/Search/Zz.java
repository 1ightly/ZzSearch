package com.example.jiejie.zzsearch.Search;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.example.jiejie.zzsearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Zz extends AppCompatActivity implements SearchView.OnQueryTextListener {
    //this is the JSON Data URL
    //make sure you are using the correct ip else it will not work
    //private static final String URL_PRODUCTS = "http://192.168.43.242/Android/Api.php";

    //a list to store all the products
    //List<Product> productList;

    //the recyclerview
    //RecyclerView recyclerView;
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView mRVFish;
    private AdapterZz mAdapter;
    private String mainQuery;
    //HttpSolrClient client=null;
    //using solr ,
    public static final int SOLR_CONNECTION_TIMEOUT=10000;
    public static final int SOLR_SOCKET_TIMEOUT=60000;
    public static final String solrUrl="http://222.20.105.25:8888/solr/indexNutch/select";

    SearchView searchView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initializing the productlist
        //productList = new ArrayList<>();

        //this method will fetch and parse json
        //to display it in recyclerview
        //loadProducts();


        //Getting the searchItem from the searchview in mainActivity
        Bundle bundle = getIntent().getExtras();
        mainQuery = bundle.getString("key");

        onQueryTextChange(mainQuery);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // adds item to action bar
        getMenuInflater().inflate(R.menu.search, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) Zz.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();

            //Insert the mainQuery from mainActivity into the searchview
            searchView.setQuery(mainQuery, true);

            //Expands the search widget once the activity is launched
            MenuItemCompat.expandActionView(searchItem);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(Zz.this.getComponentName()));
            searchView.setIconified(false);
        }

        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        new AsyncFetch(mainQuery).execute();
//        dismissKeyboard();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    public void dismissKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)    //Hides the keyboard when the button is pressed
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().
                getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
    @Override
    protected void onNewIntent(Intent intent){
        //get search query and create object of class AsyncFetch
        if(intent.ACTION_SEARCH.equals(intent.getAction())){
            String q=intent.getStringExtra(SearchManager.QUERY);
            if(searchView!=null){
                searchView.clearFocus();
            }
            new AsyncFetch(mainQuery).execute();
        }
    }
    //create new class AsyncFetch
    private class AsyncFetch extends AsyncTask<String,String,String>{
        ProgressDialog pdLoading = new ProgressDialog(Zz.this);
        HttpURLConnection conn;
        URL url = null;
        String searchQuery;
        public AsyncFetch(String searchQuery){
            this.searchQuery=searchQuery;
        }
        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params){



            try {
                //web spider URL
                url =new URL(solrUrl);
                //url=new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(SOLR_CONNECTION_TIMEOUT)
                // .withSocketTimeout(SOLR_SOCKET_TIMEOUT).build();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e.toString();
            }
            try {
                conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                //add path and parameter to our above url
                Uri.Builder builder=new Uri.Builder()
                        .appendQueryParameter("q","content:\"*"+searchQuery+"*\"")
                        .appendQueryParameter("fl","title,url")
                        .appendQueryParameter("sort","boost desc")
                        .appendQueryParameter("wt","json");
                String query=builder.build().getEncodedQuery();
                //String path=builder.build().getEncodedPath();
                OutputStream os=conn.getOutputStream();
               // conn.get
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                //writer.append(path);
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
            try {
                int reponse_code=conn.getResponseCode();
                if(reponse_code==HttpURLConnection.HTTP_OK){
                    InputStream is=conn.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                    StringBuilder result=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null){
                        result.append(line);
                    }
                    return result.toString();
                }
                else return("Connection Error");

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
            finally {
                conn.disconnect();
            }
        }
        @Override
        protected void onPostExecute(String result){
            //this method will be running on UI thread
            pdLoading.dismiss();
            List<dataZz> data=new ArrayList<>();
            System.out.print(result);
            if(result.equals("no rows")){
                Toast.makeText(Zz.this,"无查询结果",Toast.LENGTH_LONG).show();
            }
            else{
                try {
                    JSONArray jsonArray=new JSONArray(result);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject json_data=jsonArray.getJSONObject(i);
                    }
                    // Setup and Handover data to recyclerview
                    mRVFish = (RecyclerView) findViewById(R.id.recylcerView);
                    // mAdapter = new AdapterZz(Zz.this, data);
                    //mRVFish.setAdapter(mAdapter);
                    mRVFish.setLayoutManager(new LinearLayoutManager(Zz.this));

                    searchView.setQuery("", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//    public static HttpSolrClient getClient(){
//        return new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(SOLR_CONNECTION_TIMEOUT)
//         .withSocketTimeout(SOLR_SOCKET_TIMEOUT).build();
//    }
//    private void AsyncFetchSolr1(String query){
//        HttpSolrClient client=getClient();
//        List<dataZz> data=new ArrayList<>();
//        final Map<String,String> queryParamMap=new HashMap<String, String>();
//        queryParamMap.put("q","content:\"*"+query+"\"");
//        queryParamMap.put("fl","title,url");
//        queryParamMap.put("sort","title asc");
//        MapSolrParams queryParams=new MapSolrParams(queryParamMap);
//        try {
//            final QueryResponse response=client.query("test",queryParams);
//            final SolrDocumentList documents=response.getResults();
//            for(SolrDocument document:documents){
//                dataZz dataEx=new dataZz();
//                dataEx.title=(String) document.getFirstValue("title");
//                dataEx.clickUrl=(String) document.getFirstValue("url");
//                data.add(dataEx);
//            }
//            mRVFish = (RecyclerView) findViewById(R.id.recylcerView);
//            mAdapter = new AdapterZz(Zz.this, data);
//            mRVFish.setAdapter(mAdapter);
//            mRVFish.setLayoutManager(new LinearLayoutManager(Zz.this));
//
//            searchView.setQuery("", true);
//        } catch (SolrServerException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }

//    private class AsyncFetchSolr extends AsyncTask<String,String,String>{
//    ProgressDialog pdLoading = new ProgressDialog(Zz.this);
//    String searchQuery;
//    QueryResponse response = null;
//    public AsyncFetchSolr(String searchQuery){
//        this.searchQuery=searchQuery;
//    }
//    @Override
//    protected  void onPreExecute(){
//        super.onPreExecute();
//        //this method will be running on UI thread
//        pdLoading.setMessage("\tLoading...");
//        pdLoading.setCancelable(false);
//        pdLoading.show();
//    }
//    @Override
//    protected String doInBackground(String... params){
//        HttpSolrClient client=new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(SOLR_CONNECTION_TIMEOUT)
//         .withSocketTimeout(SOLR_SOCKET_TIMEOUT).build();
//         Map<String, String> queryParamMap = new HashMap<String, String>();
//        queryParamMap.put("q", "content:\"*"+searchQuery+"*\"");
//        queryParamMap.put("fl", "title,url");
//        queryParamMap.put("sort", "id title");
//        MapSolrParams queryParams = new MapSolrParams(queryParamMap);
//        try {
//            response = client.query("techproducts", queryParams);
//            //SolrDocumentList documents = response.getResults();
//        } catch (SolrServerException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "OK";
//    }
//    @Override
//    protected void onPostExecute(String result){
//        pdLoading.dismiss();
//        List<dataZz> data=new ArrayList<>();
//        if(result.equals("OK")){
//            SolrDocumentList documents = response.getResults();
//            System.out.print("Found " + documents.getNumFound() + " documents");
//            for(SolrDocument document : documents) {
//                dataZz dataEx=new dataZz();
//                dataEx.title=(String) document.getFirstValue("title");
//                dataEx.clickUrl=(String) document.getFirstValue("url");
//                data.add(dataEx);
//            }
//            mRVFish = (RecyclerView) findViewById(R.id.recylcerView);
//            mAdapter = new AdapterZz(Zz.this, data);
//            mRVFish.setAdapter(mAdapter);
//            mRVFish.setLayoutManager(new LinearLayoutManager(Zz.this));
//
//            searchView.setQuery("", true);
//
//        }
//        return;
//    }
//}
}
