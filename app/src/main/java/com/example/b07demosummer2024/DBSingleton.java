package cscb07.taam_project;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

final class DBSingleton {
    private static DBSingleton instance;
    final FirebaseDatabase db;
    final DatabaseReference db_ref;
    final FirebaseStorage storage;
    final StorageReference storage_ref;

    private DBSingleton () {
        db = FirebaseDatabase.getInstance("https://cscb07-taam-management-default-rtdb.firebaseio.com/");
        db_ref = db.getReference("data");
        storage = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com");
        storage_ref = storage.getReference("/");
    }

    public static DBSingleton getDBInstance() {
        if (instance == null) {
            instance = new DBSingleton();
        }
        return instance;
    }
}
