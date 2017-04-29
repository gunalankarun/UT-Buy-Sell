package com.a461group5.utbuysell;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by irfanhasan on 4/28/17.
 * A util class to help with firebase database operations
 */

public class DatabaseUtil {

    /**
     * This function blocks till the object is read. It's preferred not to call this on the main (UI) thread
     * @param path to database from where object will be read
     * @param clazz the class of the object to be read
     * @return Object read from database
     */
    public static Object getValue(String path, final Class<?> clazz) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(path);
        final TaskCompletionSource<Object> tcs = new TaskCompletionSource();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tcs.setResult(dataSnapshot.getValue(clazz));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(databaseError.toException());
            }
        });

        Task<Object> task = tcs.getTask();
        while(!task.isComplete()); //not sure if this is most efficient thing to do
        return task.getResult();
    }


}
