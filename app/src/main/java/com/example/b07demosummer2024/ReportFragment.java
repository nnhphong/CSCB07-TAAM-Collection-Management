package com.example.b07demosummer2024;

import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ReportFragment extends Fragment {
    FirebaseDatabase db;
    DBOperation op;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        Button btnTop = view.findViewById(R.id.btnTop);
        Button btnGenReport = view.findViewById(R.id.btnGenReport);

        db = FirebaseDatabase.getInstance();
        op = new DBOperation(db.getReference("/data"));

        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        btnGenReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtLotNumber = view.findViewById(R.id.txtLotNumber);
                EditText txtName = view.findViewById(R.id.txtName);
                TextView txtStatus = view.findViewById(R.id.txtStatus);
                Spinner dropDownCategory = view.findViewById(R.id.dropDownCategory);
                Spinner dropDownPeriod = view.findViewById(R.id.dropDownPeriod);
                CheckBox ckbDescImgOnly = view.findViewById(R.id.ckbDescPicOnly);
                final boolean[] descImgOnly = {true};

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
                ckbDescImgOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        descImgOnly[0] = isChecked;
                    }
                });
                op.searchItem(item).addOnCompleteListener(new OnCompleteListener<List<Item>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Item>> task) {
                        List<Item> result = task.getResult();
                        createReportPDF(result, descImgOnly[0]);
                    }
                });
            }
        });

        return view;
    }

    private void createReportPDF(List<Item> list, boolean descImgOnly) {
        PdfDocument pdf = new PdfDocument();
    }
}
