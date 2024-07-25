package com.example.b07demosummer2024;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class SearchFragment extends Fragment {
    private FirebaseDatabase db;
    private DBOperation op;
    public void addDropDownValue(View view, int dropDownID, int arrValuesID) {
        Spinner spinner = view.findViewById(dropDownID);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                arrValuesID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button btnTop = view.findViewById(R.id.btnTop);
        ImageButton btnSearch = view.findViewById(R.id.btnSearch);

        addDropDownValue(view, R.id.dropDownCategory, R.array.arr_category);
        addDropDownValue(view, R.id.dropDownPeriod, R.array.arr_period);

        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        DatabaseReference ref = db.getReference("/data");
        op = new DBOperation(ref);

        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtLotNumber = view.findViewById(R.id.txtLotNumber);
                EditText txtName = view.findViewById(R.id.txtName);
                TextView txtStatus = view.findViewById(R.id.txtStatus);
                Spinner dropDownCategory = view.findViewById(R.id.dropDownCategory);
                Spinner dropDownPeriod = view.findViewById(R.id.dropDownPeriod);

                String strLotNumber = txtLotNumber.getText().toString();
                String name = txtName.getText().toString();
                Integer lotNum = null;
                if (!strLotNumber.isEmpty()) {
                    try {
                        lotNum = Integer.parseInt(strLotNumber);
                    } catch (NumberFormatException e) {
                        txtStatus.setText("Error: Lot number is not a number");
                        txtStatus.setTextColor(Color.parseColor("#f54242"));
                        return;
                    }
                }

                String selectedCategory = dropDownCategory.getSelectedItem().toString();
                String selectedPeriod = dropDownPeriod.getSelectedItem().toString();

                Item item = new Item(lotNum, name, selectedCategory, selectedPeriod, "");
                op.searchItem(item).addOnCompleteListener(new OnCompleteListener<List<Item>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Item>> task) {
                        List<Item> result = task.getResult();
                        // TODO: displaying search result here
                        displayInfo(result);
                    }
                });
            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Item it = snapshot.getValue(Item.class);
                if (it != null) {
                    System.out.println(it.toString());
                } else {
                    System.out.println("load post:onCancelled");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        return view;
    }

    private void displayInfo(List<Item> l) {
        for (Item item : l) {
            System.out.println(item.getLotNumber() + " || " + item.getName() + " || " + item.getCategory() + " || " + item.getPeriod());
        }
    }

//    private void searchItem() {
//        itemsRef = db.getReference();
//    }
//    public View displaySearchRes(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, List<Item> l) {
//        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        setAdapter();
//        return view;
//    }
//
//    public void setAdapter() {
//        ItemAdapter itemAdapter = new ItemAdapter(itemList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(itemAdapter);
//    }
}
