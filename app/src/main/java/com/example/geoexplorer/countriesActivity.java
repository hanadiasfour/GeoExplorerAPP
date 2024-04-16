package com.example.geoexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class countriesActivity extends AppCompatActivity {

    private ListView countries_list_view;// view for interaction
    private TextView continentNameTitle;// title of continent
    private ArrayList<Country> countries;//hold data
    private ArrayList<String> favIDList;
    private int code=2;//default to return to main page
    private static final String SHARED_KEY="myFav";
    private static final String FAV_STRING_KEY="favorite";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_countries);

        setViews();//setting views

        if (savedInstanceState != null) {//get saved state

            favIDList = savedInstanceState.getStringArrayList("favIDList");
            countries = savedInstanceState.getParcelableArrayList("countries");
            continentNameTitle.setText(countries.get(0).getContinent());//name of contenent
            code = savedInstanceState.getInt("code");

        }else {// Get the Intent that started this activity
            Intent intent = getIntent();
            handleIntent(intent);
        }
        fillList();


        countries_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //getting the continent name selected
                Country selectedCountryName = (Country) parent.getItemAtPosition(position);
                //calling next activity
                callSpecificCountryActivity(selectedCountryName);

            }
        });
    }

    public void setViews(){
        countries_list_view= (ListView)findViewById(R.id.countries_list);
        continentNameTitle = (TextView) findViewById(R.id.continent_name_title);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // setting the new intent
        handleIntent(intent); // parsing data

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save current state
        savedInstanceState.putParcelableArrayList("countries", countries);
        savedInstanceState.putInt("code",code);
        savedInstanceState.putStringArrayList("favIDList",favIDList);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state from saved instance
            countries = savedInstanceState.getParcelableArrayList("countries");
            code = savedInstanceState.getInt("code");
            favIDList = savedInstanceState.getStringArrayList("favIDList");
    }

    private void handleIntent(Intent intent){
        // getting the ArrayLists from intent

        if(intent.hasExtra("countries_list")) {
            //it has a list of countries meaning it came from main pages
            code = intent.getIntExtra("code", 2);
            countries = intent.getParcelableArrayListExtra("countries_list");

            if (countries.size() > 0 && code == 1) {//came from the continent activity
                continentNameTitle.setText(countries.get(0).getContinent());//show name
            }
        }
        favIDList = intent.getStringArrayListExtra("favoriteIDList");
        fillList();

    }

    /*
     * back button to return to main page or continent page depending on code
     * */
    public void backToContinentsActivity(View view){

        if(code == 1){//normal route (main->continent->countries)
            Intent intent = new Intent (this,continentsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putStringArrayListExtra("favoriteIDList",favIDList);
            startActivity(intent);


        }else if (code==2){//fav route (main->fav)

            Intent intent = new Intent (this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putStringArrayListExtra("favoriteIDList",favIDList);
            startActivity(intent);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        //initializing the shared preferences and editor
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();

        //saving in the shared preferences after converting to set
        Set<String> favSet = new HashSet<String>(favIDList);
        edit.putStringSet(FAV_STRING_KEY,favSet);
        edit.apply();

    }


    /*
     * this method calls the certain country activity using explicit intent
     * */
    public void callSpecificCountryActivity(Country country){
        //creating intent and adding extras
        Intent intent = new Intent(this, certainCountryActivity.class);
        intent.putExtra("country",country);
        intent.putStringArrayListExtra("favoriteIDList",favIDList);

        //flag to bring previously created activities to top of stack
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

    }


    /*
     * creates a list of countries names to add to list view
     * */
    public void fillList(){

        Country[] array =countries.toArray(new Country[countries.size()]);
        ArrayAdapter<Country> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
        countries_list_view.setAdapter(adapter);

    }
}