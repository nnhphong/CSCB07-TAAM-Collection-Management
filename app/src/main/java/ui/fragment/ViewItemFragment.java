package ui.fragment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import cscb07.taam_project.R;
import data.database.DBOperation;
import data.database.Item;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewItemFragment extends Fragment {

    private static final String ARG_ITEM_ID = "items_id";



    private String itemsId;
    private DatabaseReference itemRef;
    private StorageReference storageRef;



    public ViewItemFragment() {
        // Required empty public constructor
    }


    public static ViewItemFragment newInstance(String itemsID) {
        ViewItemFragment fragment = new ViewItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, itemsID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemsId = getArguments().getString(ARG_ITEM_ID);
        }
//        FirebaseDatabase dbref = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
//        FirebaseStorage storage = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com");
//        dbOperation = new DBOperation(dbref.getReference("data"), storage.getReference("/"));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_view_item, container, false);

        TextView nameText = view.findViewById(R.id.itemTitle);
        TextView descriptionText = view.findViewById(R.id.itemDescription);
        TextView periodText = view.findViewById(R.id.itemPeriod);
        TextView categoryText = view.findViewById(R.id.itemCategory);
        TextView lotText = view.findViewById(R.id.itemLotNumber);
        ImageView itemImageView = view.findViewById(R.id.itemImage);

        FirebaseDatabase dbref = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com");
        DBOperation dbOperation = new DBOperation(dbref.getReference("data"), storage.getReference("/"));

        dbOperation.getItemData(itemsId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Item item = task.getResult();
                if (item != null) {
                    nameText.setText(item.getName());
                    descriptionText.setText(item.getDescription());
                    periodText.setText(item.getPeriod());
                    categoryText.setText(item.getCategory());
                    lotText.setText(String.valueOf(item.getLotNumber()));

                    if (item.getMediaLink() != null && !item.getMediaLink().isEmpty()) {
                        StorageReference imageRef = storage.getReference("/").child(item.getMediaLink());
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String url = uri.toString();
                            Glide.with(requireContext()).load(url).into(itemImageView);
                        }).addOnFailureListener(e -> {
                            System.out.println("Image not loading!");
                        });
                    }
                }
            }
        });

        return view;
    }


}