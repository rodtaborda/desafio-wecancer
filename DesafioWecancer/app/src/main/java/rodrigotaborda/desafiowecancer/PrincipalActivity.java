package rodrigotaborda.desafiowecancer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import rodrigotaborda.desafiowecancer.Fragmentos.LoginFragment;
import rodrigotaborda.desafiowecancer.R;

public class PrincipalActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //inicia o fragmento de Login
        mFragmentManager = getSupportFragmentManager();

        mFragment = new LoginFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragment).commit();
    }



}
