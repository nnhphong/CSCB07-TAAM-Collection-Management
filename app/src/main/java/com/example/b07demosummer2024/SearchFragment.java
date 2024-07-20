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

import org.w3c.dom.Text;

public class SearchFragment extends Fragment {
    public void addDropDownValue(View view, int dropDownID, int arrValuesID) {
        Spinner spinner = view.findViewById(dropDownID);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                arrValuesID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button btnTop = view.findViewById(R.id.btnTop);
        ImageButton btnSearch = view.findViewById(R.id.btnSearch);

        addDropDownValue(view, R.id.dropDownCategory, R.array.arr_category);
        addDropDownValue(view, R.id.dropDownPeriod, R.array.arr_period);

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
                Integer lotNum;
                try {
                    lotNum = Integer.parseInt(strLotNumber);
                } catch (NumberFormatException e) {
                    txtStatus.setText("Error: Lot number is not a number");
                    txtStatus.setTextColor(Color.parseColor("#f54242"));
                    return;
                }

                String selectedCategory = dropDownCategory.getSelectedItem().toString();
                String selectedPeriod = dropDownPeriod.getSelectedItem().toString();
                System.out.println(lotNum + " " + name + " " + selectedCategory + " " + selectedPeriod);
            }
        });
        return view;
    }
}
