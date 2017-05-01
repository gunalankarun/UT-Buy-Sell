package com.a461group5.utbuysell;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

/**
 * Created by irfanhasan on 4/28/17.
 * A util class to help with firebase database operations
 */

public class DatabaseUtil {

    //TODO: this does not work!!!
    /**
     * This function blocks till the object is read. It's preferred not to call this on the main (UI) thread
     * @param path to database from where object will be read
     * @param clazz the class of the object to be read
     * @return Object read from database
     */
    public static Object getValue(String path, final Class<?> clazz) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(path);
        final TaskCompletionSource<Object> tcs = new TaskCompletionSource();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tcs.setResult(dataSnapshot.getValue(clazz));
                Log.d("read", "hello");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(databaseError.toException());
            }
        });

        Task<Object> task = tcs.getTask();
        try {
            Tasks.await(task);
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        }

        return task.getResult(); //TODO: function never hits here, not sure why
    }




}
