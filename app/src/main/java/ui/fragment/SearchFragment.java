package ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

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

import cscb07.taam_project.R;
import data.database.DBOperation;
import data.database.Item;
import ui.view.Display;

public class SearchFragment extends Fragment {
    private FirebaseDatabase db;
    private DBOperation op;
    public void addDropDownValue(View view) {
        Spinner categoryDropdown = view.findViewById(R.id.dropDownCategory);
        Spinner periodDropdown = view.findViewById(R.id.dropDownPeriod);

        op.getCategories().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> categories = task.getResult();
                categories.sort(null);

                ArrayAdapter<String> category_adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, categories);
                category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                categoryDropdown.setAdapter(category_adapter);
            }
        });

        op.getPeriods().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> periods = task.getResult();
                periods.sort(null);

                ArrayAdapter<String> period_adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, periods);
                period_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                periodDropdown.setAdapter(period_adapter);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button btnTop = view.findViewById(R.id.btnTop);
        ImageButton btnSearch = view.findViewById(R.id.btnSearch);

        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        DatabaseReference ref = db.getReference("/data");
        op = new DBOperation(ref);

        Display display = new Display(this);
        addDropDownValue(view);

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
                        result.sort(null);
                        display.displaySearchRes(inflater, container, savedInstanceState,
                                result);
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
}
