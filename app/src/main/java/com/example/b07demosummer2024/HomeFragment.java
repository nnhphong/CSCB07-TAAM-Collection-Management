package com.example.b07demosummer2024;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;

    private FirebaseDatabase db;
    private FirebaseStorage storage;
    private DBOperation op;
    private boolean passedBySearch = false;

    public HomeFragment() {

    }

    public HomeFragment(List<Item> itemList) {
        this.itemList = itemList;
        this.passedBySearch = true;
        System.out.println(itemList.isEmpty());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        recyclerView = view.findViewById(R.id.mainRecyclerView);
        ImageButton btnSearchFilter = view.findViewById(R.id.btnSearchFilter);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        Button btnReport = view.findViewById(R.id.btnReport);
        Button btnView = view.findViewById(R.id.btnView);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnRemove = view.findViewById(R.id.btnRemove);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        storage = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com");
        op = new DBOperation(db.getReference("data"), storage.getReference("/"));

        if (!this.passedBySearch) {
            itemList = new ArrayList<>();
        }
        itemAdapter = new ItemAdapter(itemList, getContext());
        recyclerView.setAdapter(itemAdapter);
        if (!this.passedBySearch) {
            op.loadItems(itemList, itemAdapter);
        }
        itemAdapter.notifyDataSetChanged();

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}
        });


        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ReportFragment());
            }
        });

        btnSearchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SearchFragment());
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AddItemFragment());
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Item> selected = itemAdapter.getSelected();

                if (selected.isEmpty()) {
                    displayToast("Please select an item to remove");
                    return;
                }

                LayoutInflater inflater = LayoutInflater.from(getContext());
                View dialogView = inflater.inflate(R.layout.dialog_remove_items, null);
                LinearLayout imageContainer = dialogView.findViewById(R.id.imageContainer);

                for (Item key : selected) {
                    Integer id = key.getLotNumber();
                    TextView textView = new TextView(getContext());
                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    textView.setText(String.format("Lot Number: %d", id));
                    int paddingDp = 8;  // 8dp padding
                    int paddingPx = (int) (paddingDp * getResources().getDisplayMetrics().density);
                    textView.setPadding(paddingPx, paddingPx, paddingPx, 0);
                    imageContainer.addView(textView);

                    ImageView imageView = new ImageView(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 0, 0, paddingPx);  // Reduce margin between image and next text
                    imageView.setLayoutParams(layoutParams);
                    imageContainer.addView(imageView);

                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com");
                    StorageReference fileRef = storage.getReference("/").child(key.getMediaLink());

                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(getContext()).load(uri.toString()).into(imageView);

                    }).addOnFailureListener(e -> {
                        displayToast("Fail to load images");
                    });
                }

                AlertDialog.Builder removePopup = new AlertDialog.Builder(getContext());
                removePopup.setTitle("Remove Items");
                removePopup.setView(dialogView);
//                removePopup.setMessage("Are you sure you want to remove " + selected.size() + " items?\nLot Numbers: " + ids);
                removePopup.setPositiveButton("Yes", (dialog, which) -> {
                    for (Item item : selected) {
                        op.removeItem(item).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                op.removeImage(item).addOnCompleteListener(addTask -> {
                                    if (!addTask.isSuccessful()) {
                                        displayToast("Removing image failed: Lot Number " + item.getLotNumber());
                                        return;
                                    }
                                });
                            } else {
                                displayToast("Removing item failed: Lot Number " + item.getLotNumber());
                                return;
                            }
                        });
                    }

                    displayToast("Successfully removed " + selected.size() + " items");
                });
                removePopup.setNegativeButton("No", null);
                removePopup.show();
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void displayToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }
}
