package com.example.geoexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class continentsActivity extends AppCompatActivity {

    private ListView continents_list_view;//list to hold continents
    private ArrayList<Continent> continents;//data
    private ArrayList<String> continentNames;//names for list
    private ArrayList<String> favIDList;//hold fav id
    private static final String SHARED_KEY="myFav";
    private static final String FAV_STRING_KEY="favorite";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_continents);

        //setting view
        continents_list_view = findViewById(R.id.continent_list);


        //obtaining data from bundle
        if (savedInstanceState != null) {
            continents = savedInstanceState.getParcelableArrayList("continent");
            favIDList = savedInstanceState.getStringArrayList("favIDList");
        }


        // Get the Intent that started this activity
        Intent intent = getIntent();
        handleIntent(intent);
        fillList();



        /*
        * listener to handle list item selection
        * */
        continents_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //getting the continent selected
                Continent selectedContinentName = (Continent) parent.getItemAtPosition(position);
                callCountriesMethod(selectedContinentName);//calling next activity

            }
        });


    }

    /*
     * creates a list of continent names to add to list view
     * */
    public void fillList(){

        Continent[] array =continents.toArray(new Continent[continents.size()]);
        ArrayAdapter<Continent> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
        continents_list_view.setAdapter(adapter);

    }

    /*
    * back button to return to main page
    * */
    public void backToMain(View view){

        //creating explicit intent
        Intent intent = new Intent(this,MainActivity.class);
        intent.putStringArrayListExtra("favoriteIDList",favIDList);

        //flag to tell android to bring prev created activity to the front of stack
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);


    }

    private void handleIntent(Intent intent){
        // Retrieve the ArrayList of Parcelable objects
        if(intent.hasExtra("continents_list")){
        continents = intent.getParcelableArrayListExtra("continents_list");
        }

        favIDList = intent.getStringArrayListExtra("favoriteIDList");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // setting the new intent
        handleIntent(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save current state
        savedInstanceState.putParcelableArrayList("continent", continents);
        savedInstanceState.putStringArrayList("favIDList",favIDList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        continents = savedInstanceState.getParcelableArrayList("continent");
        favIDList = savedInstanceState.getStringArrayList("favIDList");

    }

/*
* this method calls the countries activity using explicit intent
* */
    private void callCountriesMethod(Continent continent){

        //creating intent and adding info
        Intent intent = new Intent(this, countriesActivity.class);
        intent.putParcelableArrayListExtra("countries_list",continent.getCountries());
        intent.putStringArrayListExtra("favoriteIDList",favIDList);

        //code to indicate the intent is fired from the continent activity
        intent.putExtra("code",1);

        //trigger activity
        startActivity(intent);
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


}