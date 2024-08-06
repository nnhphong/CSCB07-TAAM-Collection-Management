package cscb07.taam_project;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
    private boolean passedItemListBySearch = false;
    private EditText txtKeywordSearch;
    private Display display;

    private boolean isLoggedIn;
    private Button btnAdd;
    private Button btnReport;
    private Button btnView;
    private Button btnRemove;
    private Button btnLogin;
    private ImageButton btnSearchFilter;

    public HomeFragment(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public HomeFragment(List<Item> itemList) {
        this.itemList = itemList;
        this.passedItemListBySearch = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        getAllViews(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        storage = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com");
        DBSingleton dbSingleton = DBSingleton.getDBInstance();
        op = new DBOperation(dbSingleton.db_ref, dbSingleton.storage_ref);

        display = new Display(this);

        if (!this.passedItemListBySearch) {
            itemList = new ArrayList<>();
        }

        itemAdapter = new ItemAdapter(itemList, getContext());
        recyclerView.setAdapter(itemAdapter);

        if (!this.passedItemListBySearch) {
            op.loadItems(itemList, itemAdapter);
        }
        itemAdapter.notifyDataSetChanged();

        showBasicFunctions(inflater, container, savedInstanceState);

        if (this.isLoggedIn) {
            showAdminFunctions(view);
        }
        else {
            hideAdminFunctions(view);
        }

        return view;
    }

    private void getAllViews(View view) {
        recyclerView = view.findViewById(R.id.mainRecyclerView);
        btnSearchFilter = view.findViewById(R.id.btnSearchFilter);
        btnRemove = view.findViewById(R.id.btnRemove);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnReport = view.findViewById(R.id.btnReport);
        btnView = view.findViewById(R.id.btnView);
        txtKeywordSearch = view.findViewById(R.id.txtKeywordSearch);
        btnLogin = view.findViewById(R.id.btnLogin);
    }

    private void showBasicFunctions(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        btnSearchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SearchFragment());
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Item> selected = itemAdapter.getSelected();
                Remove removeTask = new Remove(selected, op, view);
                removeTask.remove();
            }
        });

        txtKeywordSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                System.out.println(event.getKeyCode() + " " + KeyEvent.KEYCODE_ENTER);

                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE) {
                    List<Item> result = handleKeywordSearch(txtKeywordSearch.getText().toString());
                    display.displaySearchRes(inflater, container, savedInstanceState, result);
                    return true;
                }
                return false;
            }
        });
    }

    private void showAdminFunctions(View view) {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AddItemFragment());
            }
        });

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

        // In this context, button Login will be changed to button Logout
        btnLogin.setText("Log Out");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder logoutPopup = new AlertDialog.Builder(view.getContext());
                logoutPopup.setTitle("Logging out");
                logoutPopup.setMessage("Are you sure you want to log out?");
                logoutPopup.setPositiveButton("Yes", (dialog, which) -> {
                    isLoggedIn = false;
                    hideAdminFunctions(view);
                    btnLogin.setText("Log In");
                });
                logoutPopup.setNegativeButton("No", null);
                logoutPopup.show();
            }
        });
    }

    private void hideAdminFunctions(View view) {
        display.deleteItemFromView(btnAdd);
        display.deleteItemFromView(btnReport);
        display.deleteItemFromView(btnRemove);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new LoginView());
            }
        });
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
