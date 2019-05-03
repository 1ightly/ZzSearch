package com.example.jiejie.zzsearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.jiejie.zzsearch.NavItems.About;
import com.example.jiejie.zzsearch.Search.Zz;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Zz");

        searchView=findViewById(R.id.searchview);
        SearchManager searchManager=(SearchManager)
                MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        //使用系统的SEARCH_SERVICE提供搜索功能
        searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //启动搜索界面的Activity
                Intent intent=new Intent(MainActivity.this,Zz.class);
                Bundle bundle=new Bundle();

                bundle.putString("key",s);
                intent.putExtras(bundle);
                startActivity(intent);
                searchView.setQuery("",true);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //添加菜单导航栏
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        switch(id){
            case R.id.about:
                startActivity(new Intent(this, About.class));
                break;
//            case R.id.share:
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                startActivity(Intent.createChooser(intent, "Share with"));
//                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onNewIntent(Intent intent){
        Intent intent1=new Intent(this,Zz.class);
        Bundle bundle=new Bundle();
        // Get search query and create object of class AsyncFetch
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }
            bundle.putString("query", query);
            intent1.putExtras(bundle);
            startActivity(intent1);

        }
    }
}
