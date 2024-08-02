package com.example.b07demosummer2024;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.ArrayList;

import ui.view.Display;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;

    private FirebaseDatabase db;
    private FirebaseStorage storage;
    private DBOperation op;
    private boolean passedBySearch = false;
    private EditText txtKeywordSearch;
    private Display display;

    public HomeFragment() {

    }

    public HomeFragment(List<Item> itemList) {
        this.itemList = itemList;
        this.passedBySearch = true;
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
        Button btnRemove = view.findViewById(R.id.btnRemove);
        txtKeywordSearch = view.findViewById(R.id.txtKeywordSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        storage = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com");
        op = new DBOperation(db.getReference("data"), storage.getReference("/"));

        display = new Display(this);

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

                String ids = "";

                for (int i = 0; i < selected.size(); i++) {
                    ids += selected.get(i).getLotNumber();

                    if (i < selected.size() - 1) {
                        ids += ", ";
                    }
                }

                AlertDialog.Builder removePopup = new AlertDialog.Builder(getContext());
                removePopup.setTitle("Remove Items");
                removePopup.setMessage("Are you sure you want to remove " + selected.size() + " items?\nLot Numbers: " + ids);
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

        txtKeywordSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    List<Item> result = handleKeywordSearch(txtKeywordSearch.getText().toString());
                    display.displaySearchRes(inflater, container, savedInstanceState, result);
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private List<Item> handleKeywordSearch(String keyword) {
        List<Item> searchResult = new ArrayList<>();
        keywordSearchByLotNum(keyword).continueWithTask(task -> {
            if (task.isSuccessful()) {
                List<Item> res = task.getResult();
                if (res != null) {
                    searchResult.addAll(res);
                }
            }
            return keywordSearchByName(keyword);
        }).continueWithTask(task -> {
            if (task.isSuccessful()) {
                List<Item> res = task.getResult();
                if (res != null) {
                    searchResult.addAll(res);
                }
            }
            return keywordSearchByDescription(keyword);
        }).continueWithTask(task -> {
            if (task.isSuccessful()) {
                List<Item> res = task.getResult();
                display.displayItemOnConsole(res);
                if (res != null) {
                    searchResult.addAll(res);
                }
            }
            return Tasks.forException(new NullPointerException("The End!!"));
        });
        return searchResult;
    }

    private Task<List<Item>> keywordSearchByLotNum(String keyword) {
        if (!canParseInt(keyword)) {
            return Tasks.forException(new NullPointerException(""));
        }
        int lotNum = Integer.parseInt(keyword);
        Item item = new Item(lotNum, "", "", "", "");
        return op.searchItem(item);
    }

    private Task<List<Item>> keywordSearchByName(String keyword) {
        Item item = new Item(null, keyword, "", "", "");
        return op.searchItem(item);
    }

    private Task<List<Item>> keywordSearchByDescription(String keyword) {
        Item item = new Item(null, "", "", "", keyword);
        return op.searchItem(item);
    }

    private boolean canParseInt(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException ignored) {}
        return false;
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
