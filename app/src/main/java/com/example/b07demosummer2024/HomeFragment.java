package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

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
            public void onClick(View view) {
                List<Item> selectedItems = itemAdapter.getSelected();
                if (selectedItems.isEmpty()) {
                    // Show a message to the user to select an item first
                    Toast.makeText(getContext(), "Please select an item first", Toast.LENGTH_SHORT).show();
                } else {
                    Item selectedItem = selectedItems.get(0); // Assuming you only want to view one item at a time
                    String lotNumber = String.valueOf(selectedItem.getLotNumber());
                    String itemId = "id" + lotNumber; // Generate the ID as "id" + lot number

                    ViewItemFragment fragment = ViewItemFragment.newInstance(itemId);
                    loadFragment(fragment);

                }

                }
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
}
