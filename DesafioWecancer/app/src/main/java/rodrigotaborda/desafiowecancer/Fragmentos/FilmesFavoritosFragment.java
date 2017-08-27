package rodrigotaborda.desafiowecancer.Fragmentos;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import rodrigotaborda.desafiowecancer.Modelo.Modelo;
import rodrigotaborda.desafiowecancer.R;

import com.firebase.ui.database.FirebaseRecyclerAdapter;


public class FilmesFavoritosFragment extends Fragment {

    //cria atributos
    private RecyclerView mCategoryList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private Query query;
    private DialogInterface.OnClickListener listenner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filmes_favoritos, container, false);

        //instancia o FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        //instancia o database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(mFirebaseAuth.getCurrentUser().getUid()).child("FilmesFavoritos");
        //instacia a query
        query = mDatabase.orderByChild("Titulo"); //colaca os filmes em ordem alfabética

        //referencia o objeto do recyclerview
        mCategoryList = (RecyclerView) view.findViewById(R.id.recyclerViewFilmesFavoritos);
        mCategoryList.setHasFixedSize(true);//celulas do mesmo tamanho
        mCategoryList.setLayoutManager(new LinearLayoutManager(getActivity()));//seta o layout manager


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        carregarDadosFirebase();//carrega os dados do banco de dados

    }

    public void carregarDadosFirebase(){

        //Configuracao do recyclerAdapter
        FirebaseRecyclerAdapter<Modelo, FilmesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Modelo, FilmesViewHolder>(

                Modelo.class,
                R.layout.linha_filmes_favoritos,
                FilmesViewHolder.class,
                query
        ) {

            @Override //popula o viewHolder
            protected void populateViewHolder(FilmesViewHolder viewHolder, Modelo model, final int position) {

                viewHolder.setTitulo(model.getTitulo());//funcao do modelo

                final String filmeKey = getRef(position).getKey();//pega o id do filme no bd

                //pega o click do botao de deletar em cada célula
                viewHolder.botaoDeletar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deletarFilme(filmeKey);//exclui o filme


                    }
                });



            }
        };

        //seta o adapter
        mCategoryList.setAdapter(firebaseRecyclerAdapter);
    }

    //viewHolder do RecyclerView (public - Firebase Documentation)
    public static class FilmesViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageButton botaoDeletar;

        public FilmesViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            botaoDeletar = (ImageButton) itemView.findViewById(R.id.imageButtonRecyclerDeletarFilme);

        }


        private void setTitulo(String titulo){
            TextView post_title = (TextView) mView.findViewById(R.id.textViewRecyclerFilmesFavoritos);
            post_title.setText(titulo);

        }



    }


    //funcao para excluir o filme
    private void deletarFilme (final String filmeKey){

        //pede a confirmacao do usuario
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.delete_confirmation_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {//usuario deseja deletar
                    public void onClick(DialogInterface dialog, int id) {

                        //verifica se o filme foi deletado
                        mDatabase.child(filmeKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {//filme removido

                                //mensagem de alerta
                                Toast.makeText(getActivity(), R.string.movie_deleted, Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {//erro ao excluir o filme


                                //mensagem de alerta
                                Toast.makeText(getActivity(), R.string.movie_delete_error, Toast.LENGTH_LONG).show();

                            }
                        });

                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {//usuario cancelou
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();//fecha o diálogo
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();



    }

    //Executa toda vez que o fragmento fica visível - troca de tabs
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                esconderTeclado();//esconde o teclado
            } catch (Exception e) {
                Log.i("ErrorFragmentVisibility", "Error" + e);
            }
        }
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
