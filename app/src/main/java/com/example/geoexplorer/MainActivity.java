package com.example.geoexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Continent> continents = new ArrayList<Continent>();//to store data
    private ArrayList<Country> favoritesList = new ArrayList<Country>();//store favorites
    private static final String SHARED_KEY="myFav";//key for shared pref
    private static final String FAV_STRING_KEY="favorite";// key for extra in shared pref
    private ArrayList<String> favIDList = new ArrayList<>();//store ids
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //initializing the shared pref. with the special key
        sharedPreferences = getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();

        //get saved info from bundle
        if (savedInstanceState != null) {
            continents = savedInstanceState.getParcelableArrayList("continent");
            favIDList = savedInstanceState.getStringArrayList("favIDList");
            setFavorites(favIDList);//filling list with fav countries


        }else if(continents.size()==0){
            System.out.println("read info");

            //reading input from file
            readInfo();

            //getting the string set of id of favorite countries
            Set<String> theFavSet = sharedPreferences.getStringSet(FAV_STRING_KEY, new HashSet<String>());

            // converting set to array list of the favorite ids strings
            favIDList = new ArrayList<String>(theFavSet);

            if (favIDList != null)//exist favorite countries
            {
                System.out.println("itsNotNull");
                setFavorites(favIDList);//filling list with fav countries
            }else{
                System.out.println("Main: IsNUllAgain");
            }

        }
        else{//bundle is empty and countries has been read before

            //accepting intent which triggered the activity
             Intent i = getIntent();

             //extracting updated favorite list
             favIDList = i.getStringArrayListExtra("favoriteIDList");

            if (favIDList != null)//exist favorite countries
            {
                System.out.println("itsNotNull");
                setFavorites(favIDList);//filling list with fav countries
            }else{
                System.out.println("Main: IsNUllAgain");
            }

        }

    }


    /*
     * by receiving a string list of country ids, the countries are looped and
     * added to a list. This way we can display this list when pressing the fav button
     */

    private void setFavorites(ArrayList<String> favIDList){
        System.out.println(favIDList.toString());

        //clearing all previous contents of the list to avoid duplicates
        favoritesList.clear();

        Continent continent = continents.get(0);//first continent in the list
        ArrayList<Country> countries ;//countries in a certain continent

        //looping all of the countries to set the favorites
        for(int i =0;i<continents.size();i++){

            continent = continents.get(i);//next continent
            countries = continent.getCountries();//next countries list

            for(int j=0;j<countries.size();j++){

                //obtaining country id at this index
                Country country = countries.get(j);
                String country_id = country.getId().trim();

                //set the favorite feature to true if found in saved list
                if(favIDList.contains(country_id)){
                    continent.getCountries().get(j).setFav(true);
                    if(!favoritesList.contains(country)){
                    favoritesList.add(country);
                    }
                }
            }


        }
    }


    /*
    saving the favorites list to the shared preferences whenever activity is paused
    this prevents loosing the list when unexpected lifecycle state changes
    */
    @Override
    protected void onPause() {
        super.onPause();
        //saving in the shared preferences after converting to set
        Set<String> favSet = new HashSet<String>(favIDList);
        edit.putStringSet(FAV_STRING_KEY,favSet);
        edit.apply();//apply edit

    }

    /*
    * handle any new intent which triggered this activity for a second time
    * */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // setting the new intent
        setIntent(intent);

        //extracting the given new fav list
        favIDList = intent.getStringArrayListExtra("favoriteIDList");

        if (favIDList != null)//exist favorite countries
        {
            System.out.println("itsNotNull");
            setFavorites(favIDList);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save current state (info and fav list)
        savedInstanceState.putParcelableArrayList("continent", continents);
        savedInstanceState.putStringArrayList("favIDList",favIDList);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore info from saved instance
        continents = savedInstanceState.getParcelableArrayList("continent");
        favIDList = savedInstanceState.getStringArrayList("favIDList");

    }


        /*
        triggered when pressing the explore button on the main screen
        opens the next activity displaying the continents to choose from
         */
    public void exploreButton(View view){
        //creating explicit intent and adding the info as extras
        Intent intent = new Intent(this, continentsActivity.class);
        intent.putParcelableArrayListExtra("continents_list",continents);
        intent.putStringArrayListExtra("favoriteIDList",favIDList);

        // firing the activity using intent
        startActivity(intent);
    }

    /*
       triggered when pressing the favorites button on the main screen
       opens the next activity displaying all favorite countries
        */
    public void favButton(View view){

        //creating explicit intent and adding the info as extras
        Intent intent = new Intent(this, countriesActivity.class);
        intent.putParcelableArrayListExtra("countries_list",favoritesList);
        intent.putStringArrayListExtra("favoriteIDList",favIDList);

        /*
        since the countries activity can be called by multiple sources
        a code is sent with the intent to indicate where it was sent from
        code 2 --> from the main screen
        code 1 --> from the continents screen
        */
        intent.putExtra("code",2);

        // firing the activity using intent
        startActivity(intent);


    }


/*
* reading the info from the file attached inside the assets folder
*/
    private void readInfo() {
        // Get the AssetManager
        AssetManager assetManager = getAssets();

        try {
            // Open the file
            InputStream inputStream = assetManager.open("info.txt");
            Scanner input = new Scanner(inputStream);
            input.nextLine(); // skipping header

            while (input.hasNextLine()) { // reading line by line
                Scanner read = new Scanner(input.nextLine()); // reading word by word
                read.useDelimiter(",");
                try {
                    // filling variables with values from the line
                    String id = read.next().trim();
                    String name = read.next().trim();
                    String population = read.next().trim();
                    String capital = read.next().trim();
                    String religion = read.next().trim();
                    String independance = read.next().trim();
                    String language = read.next().trim();
                    String continent = read.next().trim();

                    // determining whether all values are correct
                    if (id.isEmpty() || name.isEmpty() || population.isEmpty() || capital.isEmpty()
                            || religion.isEmpty() || independance.isEmpty() || language.isEmpty()
                            || continent.isEmpty())
                        throw new IllegalArgumentException();

                    else { // values are correct
                        Country small = new Country(id, name, capital, religion, language, population, independance,continent);
                        Continent big = new Continent(continent);

                        int index = continents.indexOf(big);

                        if (index != -1) // continent already exists
                            continents.get(index).getCountries().add(small);

                        else { // adding new Location to CDLL
                            big.getCountries().add(small);
                            continents.add(big);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("prob");
                }
                // Skipping lines with illegal inputs
            }

        } catch (IOException e1) {
            System.out.println("prob2");
        }
    }


}
