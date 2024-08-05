package ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import cscb07.taam_project.R;
import data.pdf.PDFGenerator;
import data.database.DBOperation;
import data.database.Item;

public class ReportFragment extends Fragment {
    View view;
    ImageButton btnTop;
    Button btnGenReport;
    EditText txtLotNumber;
    EditText txtName;
    TextView txtStatus;
    Spinner dropDownCategory;
    Spinner dropDownPeriod;
    CheckBox ckbDescImgOnly;

    FirebaseDatabase db;
    DBOperation op;

    PDFGenerator pdfWriter;
    boolean isChecked;

    private Toast currentToast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report, container, false);
        btnTop = view.findViewById(R.id.btnTop);
        btnGenReport = view.findViewById(R.id.btnGenReport);
        txtLotNumber = view.findViewById(R.id.txtLotNumber);
        txtName = view.findViewById(R.id.txtName);
        txtStatus = view.findViewById(R.id.txtStatus);
        dropDownCategory = view.findViewById(R.id.dropDownCategory);
        dropDownPeriod = view.findViewById(R.id.dropDownPeriod);
        ckbDescImgOnly = view.findViewById(R.id.ckbDescPicOnly);

        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        DatabaseReference FBref = db.getReference("/data");
        StorageReference ref = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com").getReference();
        op = new DBOperation(FBref);
        pdfWriter = new PDFGenerator(this);

        op.getCategories().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> categories = task.getResult();
                categories.sort(null);

                ArrayAdapter<String> category_adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, categories);
                category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                dropDownCategory.setAdapter(category_adapter);
            }
        });

        op.getPeriods().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> periods = task.getResult();
                periods.sort(null);

                ArrayAdapter<String> period_adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, periods);
                period_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                dropDownPeriod.setAdapter(period_adapter);
            }
        });

        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        if (pdfWriter.checkPermission()) {
            Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            pdfWriter.requestPermission();
        }

        ckbDescImgOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChecked = !isChecked;
            }
        });

        btnGenReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        pdfWriter.createReportPDF(result, isChecked);
                    }
                });
            }
        });

        return view;
    }
}
