package pe.edu.tecsup.firebaseapp20191;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAnalytics.getInstance(this).setUserId("dlopez");
        FirebaseAnalytics.getInstance(this).setUserProperty("fullname", "Danilo Lopez");

        Bundle bundle = new Bundle();
        bundle.putString("userid", "dlopez");
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        // Obtener le usuario Logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "usuario: " + user);
        if(user != null )
            Log.d(TAG, "Nombres: " + user.getDisplayName());

        // Recuperar el InstanceID
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        Log.e(TAG, "InstanceId: " + token);
                    }
                });


        // Recuperar el usuario logueado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        User usuario = new User();
        usuario.setUid(currentUser.getUid());
        usuario.setDisplayName(currentUser.getDisplayName());
        usuario.setEmail(currentUser.getEmail());
        if(currentUser.getPhotoUrl() != null)
            usuario.setPhotoUrl(currentUser.getPhotoUrl().toString());

        FirebaseDatabase.getInstance().getReference("users")
                .child(usuario.getUid())
                .setValue(usuario);

        FirebaseDatabase.getInstance().getReference("users")
                .child(usuario.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        setTitle(user.getDisplayName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                // Cerrar la Sesion
                callLogout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callLogout(){
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
