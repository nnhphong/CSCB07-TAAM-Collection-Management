package ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cscb07.taam_project.HomeFragment;
import cscb07.taam_project.Item;
import cscb07.taam_project.ItemAdapter;
import cscb07.taam_project.R;

import java.util.List;

public class Display {
    private final Fragment displayOn;
    public Display(Fragment displayOn) {
        this.displayOn = displayOn;
    }

    public void displaySearchRes(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState, List<Item> itemList) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.mainRecyclerView);
        ItemAdapter itemAdapter = new ItemAdapter(itemList, displayOn.getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(displayOn.getContext()));
        recyclerView.setAdapter(itemAdapter);

        loadFragment(new HomeFragment(itemList));
    }

    public void displayItemOnConsole(List<Item> l) {
        for (Item item : l) {
            System.out.println(item.getLotNumber() + " || " + item.getName() + " || " + item.getCategory() + " || " + item.getPeriod());
        }
    }

    public void deleteItemFromView(View obj) {
        obj.setEnabled(false);
        ViewGroup parent = (ViewGroup) obj.getParent();
        parent.removeView(obj);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = displayOn.getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
