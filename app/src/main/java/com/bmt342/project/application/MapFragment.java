package com.bmt342.project.application;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmt342.project.application.model.Post;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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

    View view;
    DatabaseReference database;
    ArrayList<LatLng> locationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_map, container, false);
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MapFragmentContainerView);
            mapFragment.getMapAsync(this);

            database = FirebaseDatabase.getInstance().getReference("posts");
            locationList = new ArrayList<>();

            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (locationList.isEmpty()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            double latitude = dataSnapshot.child("address").child("latitude").getValue(Double.class);
                            double longitude = dataSnapshot.child("address").child("longitude").getValue(Double.class);
                            LatLng latLng = new LatLng(latitude, longitude);
                            locationList.add(latLng);
                        }
                    }
                    if (mMap != null) {
                        mMap.clear();
                        addHeatMap();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        return view;
    }

    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLngBounds turkeyBounds = new LatLngBounds(
                new LatLng(35.0, 26.0),
                new LatLng(42.0, 45.0)
        );

        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(18.0f);

        mMap.setLatLngBoundsForCameraTarget(turkeyBounds);

        LatLng ankaraLatlng = new LatLng(37.409125,36.094003);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ankaraLatlng, 5);
        mMap.moveCamera(cameraUpdate);
    }

    private void addHeatMap() {
        List<WeightedLatLng> weightedLatLngs = new ArrayList<>();

        for (LatLng latLng : locationList) {
            weightedLatLngs.add(new WeightedLatLng(latLng,1));
        }

        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .weightedData(weightedLatLngs)
                .radius(50).build();

        TileOverlay overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }
}