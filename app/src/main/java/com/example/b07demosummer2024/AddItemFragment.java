package com.example.b07demosummer2024;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class AddItemFragment extends Fragment {
    // Elements of fragment
    private EditText lotNumberInput, nameInput, descriptionInput;
    private Spinner categoryInput, periodInput;
    private ImageButton backButton, addImage, submit;

    private Uri selectedMedia;

    // Database connection
    private FirebaseDatabase db;
    private DatabaseReference itemRef;
    private FirebaseStorage mediaRef;

    // Constant used for image picking
    private static final int REQUEST_MEDIA_PICK = 1;

    private List<Item> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        // Get elements from the view
        lotNumberInput = view.findViewById(R.id.lotNumberInput);
        nameInput = view.findViewById(R.id.nameInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        categoryInput = view.findViewById(R.id.categorySpinner);
        periodInput = view.findViewById(R.id.periodSpinner);
        backButton = view.findViewById(R.id.backButton);
        addImage = view.findViewById(R.id.addImageButton);
        submit = view.findViewById(R.id.addItemButton);

        // Connect to database
        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        itemRef = db.getReference("data");
        mediaRef = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com");

        // Set spinner options
        ArrayAdapter<CharSequence> category_adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.arr_category, android.R.layout.simple_spinner_item
        );
        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        categoryInput.setAdapter(category_adapter);

        ArrayAdapter<CharSequence> period_adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.arr_period, android.R.layout.simple_spinner_item
        );
        period_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        periodInput.setAdapter(period_adapter);

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
                getParentFragmentManager().popBackStack();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
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

    public void addItem() {
        String lotNumber = lotNumberInput.getText().toString();
        String name = nameInput.getText().toString();
        String description = descriptionInput.getText().toString();
        String category = categoryInput.getSelectedItem().toString();
        String period = periodInput.getSelectedItem().toString();

        if (lotNumber.isEmpty() || name.isEmpty() || description.isEmpty() || category.isEmpty() || period.isEmpty()) {
            Toast.makeText(this.getActivity(), "Please fill out all fields", Toast.LENGTH_LONG).show();
            return;
        }

        Item toAdd = new Item(Integer.parseInt(lotNumber), name, category, period, description);
        Item lotNumberCheck = new Item();
        lotNumberCheck.setLotNumber(toAdd.getLotNumber());
        DBOperation op = new DBOperation(itemRef);

        op.searchItem(lotNumberCheck).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Item> sameLotNumber = task.getResult();

                if (sameLotNumber.isEmpty()) {
                    System.out.println(sameLotNumber.toString());
                    op.addItem(toAdd, this).addOnCompleteListener(addTask -> {
                        if (addTask.isSuccessful()) {
                            displayToast("Item added successfully");
                            getParentFragmentManager().popBackStack();
                        } else {
                            displayToast("Failed to add item");
                        }
                    });
                } else {
                    displayToast("Lot Number already exists");
                }
            }
        });
    }

    public void receiveData(List<Item> data) {
        this.data = data;
    }

    public void displayToast(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
