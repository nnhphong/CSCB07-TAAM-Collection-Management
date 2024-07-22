package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchFragment extends Fragment {
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private Button buttonSearch;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button btnTop = view.findViewById(R.id.btnTop);
        buttonSearch = view.findViewById(R.id.btnSearch);

        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");

        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Item it = snapshot.getValue(Item.class);
                if (it != null)
                {
                    System.out.println(it.toString());
                }
                else
                {
                    System.out.println("load post:onCancelled");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        return view;
    }
    private void searchItem() {
        itemsRef = db.getReference();
    }
    public View displaySearchRes(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, List<Item> l) {
	    View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        setAdapter();
	    return view;
    }
	
    public void setAdapter() {
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter);
    }
    
}
