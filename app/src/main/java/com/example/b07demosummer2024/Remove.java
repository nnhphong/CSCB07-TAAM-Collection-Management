package cscb07.taam_project;

import android.app.AlertDialog;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.View;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Remove {
    private final List<Item> selected;
    private final DBOperation op;
    private View view;

    public Remove(List<Item> selected, DBOperation op, View view) {
        this.selected = selected;
        this.op = op;
        this.view = view;
    }

    public void remove() {
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

        AlertDialog.Builder removePopup = new AlertDialog.Builder(view.getContext());
        removePopup.setTitle("Remove Items");
        removePopup.setMessage("Are you sure you want to remove " + selected.size() + " items?\nLot Numbers: " + ids);
        removePopup.setPositiveButton("Yes", (dialog, which) -> {
            for (Item item : selected) {
                op.removeItem(item).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        op.removeImage(item).addOnCompleteListener(addTask -> {
                            if (!addTask.isSuccessful()) {
                                displayToast("Removing image failed: Lot Number " + item.getLotNumber());
                            }
                        });
                    } else {
                        displayToast("Removing item failed: Lot Number " + item.getLotNumber());
                    }
                });
            }

            displayToast("Successfully removed " + selected.size() + " items");
        });
        removePopup.setNegativeButton("No", null);
        removePopup.show();

    }

    public void displayToast(String message) {
        /*
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
         */
        Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
    }

}
