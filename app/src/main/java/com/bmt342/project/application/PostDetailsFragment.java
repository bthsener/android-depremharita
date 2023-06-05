package com.bmt342.project.application;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmt342.project.application.model.Post;
import com.bmt342.project.application.utils.SharedPreferenceUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDetailsFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostDetailsFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostDetailsFragment newInstance(String param1, String param2) {
        PostDetailsFragment fragment = new PostDetailsFragment();
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

    public static PostDetailsFragment newInstance() {
        return new PostDetailsFragment();
    }

    Post post;
    TextView titleTextView, contentTextView, addressLineTextView;
    ImageView image;
    Button deletePostBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(getContext());

        deletePostBtn = view.findViewById(R.id.deletePostBtn);
        titleTextView = view.findViewById(R.id.titleDetails);
        contentTextView = view.findViewById(R.id.contentDetails);
        addressLineTextView = view.findViewById(R.id.AddressLineDetails);
        image = view.findViewById(R.id.imageDetails);

        if (sharedPreferenceUtil.getLoginStatus()){
            deletePostBtn.setVisibility(view.VISIBLE);
        }else {
            deletePostBtn.setVisibility(view.GONE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.detailsMapFragment);
        mapFragment.getMapAsync(this);

        Bundle bundle = getArguments();
        post = (Post) bundle.getSerializable("post");

        titleTextView.setText(post.getTitle());
        contentTextView.setText(post.getContent());
        //contentTextView.setMovementMethod(new ScrollingMovementMethod());
        addressLineTextView.setText(post.getAddress().getAddressLine());
        image.setImageBitmap(post.convertStringToBitmap(post.getImageUrl()));


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("posts");
        String postKey = post.getPostKey();
        DatabaseReference deleteRef = myRef.child(postKey);

        deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Post başarıyla silindi", Toast.LENGTH_SHORT).show();
                            //Navigation.findNavController(view).navigate(R.id.action_postDetailsFragment_to_postFragment);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Navigation.findNavController(view).navigate(R.id.action_postDetailsFragment_to_postFragment);
                                }
                            }, 500);

                        } else {
                            Toast.makeText(getActivity(), "Silme işlemi başarısız", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return view;
    }

    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng latLng = new LatLng(post.getAddress().getLatitude(), post.getAddress().getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
        mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mMap.moveCamera(cameraUpdate);
    }
}