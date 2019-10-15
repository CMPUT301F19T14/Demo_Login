package com.example.simpleparadox.listycity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Declare the variables so that you will be able to reference it later.
    ListView cityList;
    ArrayAdapter<City> cityAdapter;
    ArrayList<City> cityDataList;

    CustomList customList;

    String citySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final String TAG="Sample";
        Button addCityButton;
        final EditText addCityEditText;
        final EditText addProvinceEditText;

        FirebaseFirestore db;

        addCityButton = findViewById(R.id.add_city_button);
        addCityEditText=findViewById(R.id.add_cty_field);
        addProvinceEditText=findViewById(R.id.add_province_field);
        Button addCityDeleteButton =findViewById(R.id.del_city_button);

        cityList = findViewById(R.id.city_list);

//        String []cities ={"Edmonton", "Vancouver", "Toronto", "Hamilton", "Denver", "Los Angeles"};
//        String []provinces = {"AB", "BC", "ON", "ON", "CO", "CA"};


        cityDataList = new ArrayList<>();

//        for(int i=0;i<cities.length;i++){
//            cityDataList.add((new City(cities[i], provinces[i])));
//        }

        cityAdapter = new CustomList(this, cityDataList);

        cityList.setAdapter(cityAdapter);

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference=db.collection("Cities");

        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView_City=view.findViewById(R.id.city_text);
                citySelected=textView_City.getText().toString();
                Log.i("City",citySelected);
            }
        });

        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String cityName=addCityEditText.getText().toString();
                final String provinceName=addProvinceEditText.getText().toString();

                HashMap<String,String> data = new HashMap<>();

                if (cityName.length()>0 && provinceName.length()>0){
                    data.put("province_name",provinceName);

                    collectionReference
                            .document(cityName)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"Data addition successful");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"Data addition failed"+e.toString());
                                }
                            });

                    addCityEditText.setText("");
                    addProvinceEditText.setText("");


                    collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            cityDataList.clear();
                            for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                                Log.d(TAG,String.valueOf(doc.getData().get("province_name")));
                                String city= doc.getId();
                                String province=(String) doc.getData().get("province_name");
                                cityDataList.add(new City(city,province));
                            }

                            cityAdapter.notifyDataSetChanged();
                        }

                    });


                }
            }
        });


    addCityDeleteButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(citySelected==null){
                return;
            }

            collectionReference.document(citySelected)
                    .delete();

        }
    });




    }




}
