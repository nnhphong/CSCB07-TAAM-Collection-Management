package com.example.b07demosummer2024;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

public class AddItemFragment extends Fragment {
    // Elements of fragment
    private EditText lotNumber, name, description;
    private Spinner category, period;
    private ImageButton backButton, addImage, submit;

    private Uri selectedMedia;

    // Database connection
    private FirebaseDatabase db;
    private DatabaseReference itemRef;

    // Constant used for image picking
    private static final int REQUEST_MEDIA_PICK = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        // Get elements from the view
        lotNumber = view.findViewById(R.id.lotNumberInput);
        name = view.findViewById(R.id.nameInput);
        description = view.findViewById(R.id.descriptionInput);
        category = view.findViewById(R.id.categorySpinner);
        period = view.findViewById(R.id.periodSpinner);
        backButton = view.findViewById(R.id.backButton);
        addImage = view.findViewById(R.id.addImageButton);
        submit = view.findViewById(R.id.addItemButton);

        // Connect to database
        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");

        // Set spinner options
        ArrayAdapter<CharSequence> category_adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.arr_category, android.R.layout.simple_spinner_item
        );
        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        category.setAdapter(category_adapter);

        ArrayAdapter<CharSequence> period_adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.arr_period, android.R.layout.simple_spinner_item
        );
        period_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        period.setAdapter(period_adapter);

        // Selecting image
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageVideo();
            }
        });

        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void pickImageVideo() {
        // Set up an intent to select an image or video
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, REQUEST_MEDIA_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the selected image/video is valid
        if (requestCode == REQUEST_MEDIA_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedMedia = data.getData();
        }
    }
}
