package rodrigotaborda.desafiowecancer.TabAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import rodrigotaborda.desafiowecancer.Fragmentos.AdicionarFilmeFragment;
import rodrigotaborda.desafiowecancer.Fragmentos.FilmesFavoritosFragment;
import rodrigotaborda.desafiowecancer.Fragmentos.PerfilFragment;


public class TabAdapter extends FragmentStatePagerAdapter {

    //titulo das abas
    private String[] tituloAbas = {"Adicionar Filme ", "Favoritos", "Perfil"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new AdicionarFilmeFragment();

                break;
            case 1:
                fragment = new FilmesFavoritosFragment();
                break;
            case 2:
                fragment = new PerfilFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tituloAbas.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tituloAbas[position];


    }


}
