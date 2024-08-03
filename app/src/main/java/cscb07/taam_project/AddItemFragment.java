package cscb07.taam_project;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;
import android.provider.OpenableColumns;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddItemFragment extends Fragment {
    // Elements of fragment
    private EditText lotNumberInput, nameInput, descriptionInput;
    private AutoCompleteTextView categoryInput, periodInput;
    private ImageButton backButton, addImage, submit, removeImage, categoryDropdown, periodDropdown;
    private ConstraintLayout selectedMediaDisplay;
    private TextView selectedMediaNameDisplay;

    private Uri selectedMedia;

    // Database connection
    private FirebaseDatabase db;
    private DatabaseReference itemRef;
    private FirebaseStorage storage;
    private StorageReference mediaRef;
    private DBOperation op;

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
        categoryInput = view.findViewById(R.id.categoryInputBox);
        periodInput = view.findViewById(R.id.periodInputBox);
        backButton = view.findViewById(R.id.backButton);
        addImage = view.findViewById(R.id.addImageButton);
        submit = view.findViewById(R.id.addItemButton);
        removeImage = view.findViewById(R.id.removeImage);
        selectedMediaDisplay = view.findViewById(R.id.selectedMediaDisplay);
        selectedMediaNameDisplay = view.findViewById(R.id.imageSelected);
        categoryDropdown = view.findViewById(R.id.categoryDropdownButton);
        periodDropdown = view.findViewById(R.id.periodDropdownButton);

        // Connect to database
        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        itemRef = db.getReference("data");
        storage = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com");
        mediaRef = storage.getReference("media");
        op = new DBOperation(itemRef, mediaRef);

        // Set spinner options
        op.getCategories().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> categories = task.getResult();
                categories.sort(null);

                ArrayAdapter<String> category_adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, categories);
                category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                categoryInput.setAdapter(category_adapter);
            }
        });

        op.getPeriods().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> periods = task.getResult();
                periods.sort(null);

                ArrayAdapter<String> period_adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, periods);
                period_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                periodInput.setAdapter(period_adapter);
            }
        });

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

        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedMedia = null;
                selectedMediaDisplay.setVisibility(View.GONE);
            }
        });

        categoryDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryInput.showDropDown();
            }
        });

        periodDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                periodInput.showDropDown();
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

            selectedMediaDisplay.setVisibility(View.VISIBLE);
            System.out.println(selectedMedia.getPath());
            selectedMediaNameDisplay.setText(getFileName(selectedMedia));
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        ContentResolver contentResolver = getActivity().getContentResolver();

        if (uri != null) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                try {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(nameIndex);
                    }
                } finally {
                    cursor.close();
                }
            }
        }

        return fileName;
    }

    private void addItem() {
        String lotNumber = lotNumberInput.getText().toString();
        String name = nameInput.getText().toString();
        String description = descriptionInput.getText().toString();
        String category = categoryInput.getText().toString();
        String period = periodInput.getText().toString();

        if (lotNumber.isEmpty() || name.isEmpty() || description.isEmpty() || category.isEmpty() || period.isEmpty()) {
            displayToast("Please fill out all fields");
            return;
        } else if (selectedMedia == null) {
            displayToast("Please select an image");
            return;
        }

        Item toAdd = new Item(Integer.parseInt(lotNumber), name, category, period, description);
        Item lotNumberCheck = new Item();
        lotNumberCheck.setLotNumber(toAdd.getLotNumber());

        op.searchItem(lotNumberCheck).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Item> sameLotNumber = task.getResult();

                if (sameLotNumber.isEmpty()) {
                    UploadTask uploadTask = op.addImage(selectedMedia, this, toAdd.getLotNumber());

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (!uploadTask.isSuccessful()) {
                                displayToast("Image upload failed");
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (uploadTask.isSuccessful()) {
                                ContentResolver content = getActivity().getContentResolver();

                                if (content.getType(selectedMedia).startsWith("image/")) {
                                    toAdd.setMediaType("image");
                                } else {
                                    toAdd.setMediaType("video");
                                }

                                toAdd.setMediaLink("media/id" + toAdd.getLotNumber());

                                op.addItem(toAdd, AddItemFragment.this).addOnCompleteListener(addTask -> {
                                    if (addTask.isSuccessful()) {
                                        displayToast("Item added successfully");
                                        getParentFragmentManager().popBackStack();
                                    } else {
                                        displayToast("Failed to add item");
                                    }
                                });
                            }
                        }
                    });
                } else {
                    displayToast("Lot Number already exists");
                }
            }
        });
    }

    public void displayToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }
}
