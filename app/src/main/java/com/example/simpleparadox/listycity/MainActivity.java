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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    final String TAG = "Sample";

    ListView cityList;
    ArrayAdapter<City> cityAdapter;
    ArrayList<City> cityDataList;


    String citySelected;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
        String email="some@some.com";
        String password="123456";

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });

        Button addCityButton = findViewById(R.id.add_city_button);
        final EditText addCityEditText = findViewById(R.id.add_cty_field);
        final EditText addProvinceEditText = findViewById(R.id.add_province_field);
        Button addCityDeleteButton = findViewById(R.id.del_city_button);

        cityDataList = new ArrayList<>();
        cityAdapter = new CustomList(this, cityDataList);
        cityList = findViewById(R.id.city_list);
        cityList.setAdapter(cityAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Cities");


        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView_City = view.findViewById(R.id.city_text);
                citySelected = textView_City.getText().toString();
                Log.i("City", citySelected);
            }
        });

        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String cityName = addCityEditText.getText().toString();
                final String provinceName = addProvinceEditText.getText().toString();

                HashMap<String, String> data = new HashMap<>();

                if (cityName.length() > 0 && provinceName.length() > 0) {
                    data.put("province_name", provinceName);

                    collectionReference
                            .document(cityName)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Data addition successful");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Data addition failed" + e.toString());
                                }
                            });

                    addCityEditText.setText("");
                    addProvinceEditText.setText("");

                }
            }
        });

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                cityDataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Log.d(TAG, String.valueOf(doc.getData().get("province_name")));
                    String city = doc.getId();
                    String province = (String) doc.getData().get("province_name");
                    cityDataList.add(new City(city, province));
                }
                cityAdapter.notifyDataSetChanged();
            }

        });


        addCityDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (citySelected == null) {
                    return;
                }

                collectionReference.document(citySelected)
                        .delete();

            }
        });


    }


}
