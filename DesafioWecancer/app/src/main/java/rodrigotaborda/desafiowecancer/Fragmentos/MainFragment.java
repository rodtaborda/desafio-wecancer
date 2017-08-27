package rodrigotaborda.desafiowecancer.Fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import rodrigotaborda.desafiowecancer.R;
import rodrigotaborda.desafiowecancer.TabAdapter.SlidingTabLayout;
import rodrigotaborda.desafiowecancer.TabAdapter.TabAdapter;


public class MainFragment extends Fragment  {

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListenner;
    private TextView textViewSair;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        //Referencia os objetos
        slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) view.findViewById(R.id.vp_pagina);
        textViewSair = (TextView) view.findViewById(R.id.textViewSair);

        //Configura o TabAdapter
        TabAdapter tabAdapterProfile = new TabAdapter(getActivity().getSupportFragmentManager());

        //Seta o adapter
        viewPager.setAdapter(tabAdapterProfile);

        //Seta o viewPagert
        slidingTabLayout.setViewPager(viewPager);

        //Distribui igualmente as tabs
        slidingTabLayout.setDistributeEvenly(true);

        //Cor das tabs (letras e base)
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(getContext(),R.color.colorPrimary));



        //Identifica clique no textView da toolbar
        textViewSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logOut();//desloga o usuário

                esconderTeclado();//esconde o teclado

            }
        });


        return view;
    }



    //funcao para deslogar do firebase
    public void logOut() {

        FirebaseAuth.getInstance().signOut();

        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LoginFragment loginFragment = new LoginFragment();

        fragmentTransaction.replace(R.id.fragment_container, loginFragment).commit();
    }

    //funcao para esconder o teclado
    private void esconderTeclado() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
