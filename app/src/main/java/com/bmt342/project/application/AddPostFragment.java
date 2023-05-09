package com.bmt342.project.application;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddPostFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AddPostFragment() {
        // Required empty public constructor
    }

    public static AddPostFragment newInstance(String param1, String param2) {
        AddPostFragment fragment = new AddPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    private Button cameraButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.PostMapFragment);
        mapFragment.getMapAsync(this);

        mImageView = view.findViewById(R.id.addImage);
        cameraButton = view.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        return view;
    }

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private void dispatchTakePictureIntent() {
        // Kamera izni kontrolü
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Kullanıcıdan kamera izni isteme
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // İzinler verildiyse kamera açma intenti başlat
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }


    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng latLng = new LatLng(36.216598, 36.152493);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
        mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mMap.moveCamera(cameraUpdate);
    }

    private void updateMap(double latitude, double longitude) {
        LatLng location = new LatLng(latitude, longitude);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(location).title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }


    Slider slider;
    TextView sliderPerson;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        addressTextView = view.findViewById(R.id.addressTextView);
        Button GetLocationBtn = view.findViewById(R.id.fragmentGetLOcationBtn);
        GetLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws SecurityException{
                getLastLocation();
            }
        });


        slider = view.findViewById(R.id.sliderPerson);
        sliderPerson = view.findViewById(R.id.person);
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                sliderPerson.setText("İhtiyaç sahibi kişi sayısı: "+(int)value);
            }
        });


    }

    FusedLocationProviderClient fusedLocationProviderClient;
    double lalitude, longitude;
    TextView addressTextView;
    private final static  int REQUEST_CODE=100;
    private com.bmt342.project.application.model.Location locationT;

    public void getLastLocation(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null){
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            lalitude = addresses.get(0).getLatitude();
                            longitude = addresses.get(0).getLongitude();
                            System.out.println(lalitude+" "+longitude);
                            addressTextView.setText("ADRES: "+addresses.get(0).getAddressLine(0));
                            updateMap(lalitude,longitude);

                            //locationT.setLatitude(lalitude);
                            //locationT.setLongitude(longitude);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

        }else{
            askPermission();
        }
    }

    public  void askPermission(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_LOCATION = 200;

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_CAMERA);
        } else {
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Kamera izni verildi", Toast.LENGTH_SHORT).show();
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(getContext(), "Kamera izni gerekiyor", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Konum izni verildi", Toast.LENGTH_SHORT).show();
                    getLastLocation();
                } else {
                    Toast.makeText(getContext(), "Konum izni gerekiyor", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


}