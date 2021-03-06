package com.michalgerhat.fimme;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Locale;

public class PlacesActivity extends AppCompatActivity
{
    private PlacesManager placesManager;
    private LocationObject myLocation;
    private ListView listPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Intent intent = this.getIntent();
        Bundle locationBundle = intent.getExtras();
        myLocation = (LocationObject) locationBundle.getSerializable("MY_LOCATION");

        placesManager = new PlacesManager(this);

        listPlaces = findViewById(R.id.listPlaces);
        FloatingActionButton fabAddPlace = findViewById(R.id.fabAddPlace);

        final PlacesAdapter adapter = new PlacesAdapter(this, placesManager.placesArray);
        adapter.setDeleteButtonListener(new PlacesAdapter.IDeleteButtonListener()
        {
            @Override
            public void OnButtonClickListener(int position, LocationObject place)
            {
                placesManager.removePlace(place);
                adapter.notifyDataSetChanged();
            }
        });
        listPlaces.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationObject target = (LocationObject) listPlaces.getAdapter().getItem(position);
                Intent returnIntent = new Intent(listPlaces.getContext(), MainActivity.class);
                Bundle locationBundle = new Bundle();
                locationBundle.putSerializable("TARGET_LOCATION", target);
                returnIntent.putExtras(locationBundle);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        fabAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlacesActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_addplace, null);

                final EditText txtPlaceName = dialogView.findViewById(R.id.txtPlaceName);
                final EditText txtPlaceLat = dialogView.findViewById(R.id.txtPlaceLat);
                final EditText txtPlaceLon = dialogView.findViewById(R.id.txtPlaceLon);
                final EditText txtPlaceAlt = dialogView.findViewById(R.id.txtPlaceAlt);

                builder.setView(dialogView)
                        .setPositiveButton("Save place", null)
                        .setNeutralButton("Use GPS", null)
                        .setNegativeButton("Cancel", null);

                final AlertDialog dialog = builder.create();
                dialog.show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = txtPlaceName.getText().toString();
                        String latString = txtPlaceLat.getText().toString();
                        String lonString = txtPlaceLon.getText().toString();
                        String altString = txtPlaceAlt.getText().toString();

                        if (name.length() != 0 && latString.length() != 0 && lonString.length() != 0 && altString.length() != 0) {
                            double lat = Double.parseDouble(latString);
                            double lon = Double.parseDouble(lonString);
                            double alt = Double.parseDouble(altString);
                            placesManager.addPlace(new LocationObject(name, lat, lon, alt));
                            dialog.dismiss();
                        }
                    }
                });

                Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtPlaceLat.setText(String.format(Locale.US, "%.8f", myLocation.lat));
                        txtPlaceLon.setText(String.format(Locale.US, "%.8f", myLocation.lon));
                        txtPlaceAlt.setText(String.format(Locale.US, "%.8f", myLocation.alt));
                    }
                });

                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}
