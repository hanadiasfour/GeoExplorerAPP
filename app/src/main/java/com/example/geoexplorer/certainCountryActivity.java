package com.example.geoexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class certainCountryActivity extends AppCompatActivity {

    //views
    private TextView name,capital,language,population,year,continent,religion;
    private ImageView map;
    private CheckBox favorite;
    private ArrayList<String> favIDList;//data
    private static final String SHARED_KEY="myFav";
    private static final String FAV_STRING_KEY="favorite";
    private Country country;//data




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_certain_country);

        //setting the views
        setViews();

        if (savedInstanceState != null) {//get saved state
            country = savedInstanceState.getParcelable("country");
            favIDList = savedInstanceState.getStringArrayList("favIDList");
            setValues();
        }
            // Get the Intent that started this activity
            Intent intent = getIntent();
            handleIntent(intent);


        //listener to handle favorite box checking
            favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (favIDList != null) {
                        if (isChecked) {// Checkbox is checked

                            //add to list
                            if (!favIDList.contains(country.getId()))
                                favIDList.add(country.getId());

                        } else {// Checkbox is unchecked

                            //remove from list
                            if (favIDList.contains(country.getId()))
                                favIDList.remove(country.getId());

                        }
                    }
                }
            });
        }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save current state
        savedInstanceState.putParcelable("country", country);
        savedInstanceState.putStringArrayList("favIDList",favIDList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        country = savedInstanceState.getParcelable("country");
        favIDList = savedInstanceState.getStringArrayList("favIDList");


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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // setting the new intent
        handleIntent(intent); // parsing data
    }

    private void handleIntent(Intent intent){

        // getting the ArrayLists from intent
        favIDList = intent.getStringArrayListExtra("favoriteIDList");
        country = intent.getParcelableExtra("country");
        setValues();//filling views with info

    }

    /*
     * back button to return to countries activity
     * by creating explicit intent and setting flag and extra
     * */
    public void backFromCertainCountryActivity(View view){

        //creating intent
        Intent in = new Intent(this,countriesActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        in.putStringArrayListExtra("favoriteIDList",favIDList);

        //triggering activity
        startActivity(in);

    }


    /*
    * uses information inside the country object to fill in the informative views
    * */
    private void setValues(){

        if (country != null) {
            // filling information:
            name.setText(country.getName());
            capital.setText(country.getCapital());
            language.setText(country.getLanguage());
            population.setText(country.getPopulation());
            year.setText(country.getIndependance());
            continent.setText(country.getContinent());
            religion.setText(country.getReligion());

            //setting checkbox
            if(favIDList!=null && favIDList.contains(country.getId()))
                favorite.setChecked(true);
            else
                favorite.setChecked(false);


            //setting the image
            String imageName = "img" + country.getId();
            int resId = getResources().getIdentifier(imageName,"drawable",getPackageName());
            map.setImageResource(resId);

        }

    }

    private void setViews(){//sets views
        name = (TextView)findViewById(R.id.country_name);
        capital= (TextView)findViewById(R.id.capital);
        language= (TextView)findViewById(R.id.language);
        population= (TextView)findViewById(R.id.population);
        year= (TextView)findViewById(R.id.independence);
        continent= (TextView)findViewById(R.id.continent);
        religion= (TextView)findViewById(R.id.religion);
        map= (ImageView) findViewById(R.id.country_pic);
        favorite= (CheckBox) findViewById(R.id.checkbox);


    }


}