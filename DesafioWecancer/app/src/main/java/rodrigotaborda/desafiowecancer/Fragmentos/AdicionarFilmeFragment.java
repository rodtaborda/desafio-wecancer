package rodrigotaborda.desafiowecancer.Fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import rodrigotaborda.desafiowecancer.R;


public class AdicionarFilmeFragment extends Fragment {

    //Cria atributos
    private EditText filmeFavorito;
    private Button botaoEnviarFilme;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adicionar_filme, container, false);

        //Referencia objetos
        filmeFavorito = (EditText) view.findViewById(R.id.editTextFilmeFavorito);
        botaoEnviarFilme = (Button) view.findViewById(R.id.buttonEnviarFilme);
        mProgressDialog = new ProgressDialog(getActivity());

        //Instancia o mFirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Instancia o FirebaseDatabase
        databaseReference = FirebaseDatabase.getInstance().getReference();



        //Reconhece o click na tela
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();//esconde o teclado

            }
        });

        //reconhece click no botao
        botaoEnviarFilme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enviarFilme();//envia novo filme


            }
        });


        return view;
    }



    //funçao para enviar o filme
    private void enviarFilme(){

        //checa se o edit text foi preenchido
        if (filmeFavorito.getText().toString().equals("")){//ET vazio

            //mensagem de alerta
            Toast.makeText(getActivity(), R.string.movie_empty, Toast.LENGTH_SHORT).show();

        }else{//ET preenchido

            //Mostra dialogo de carregamento
            mProgressDialog.setMessage(getResources().getString(R.string.loading));
            mProgressDialog.show();

            //verificar se o filme já foi enviado
            verificaFilmeAdicionado();

        }

    }


    //funcao para verificar se o filme já foi adicionado anteriormento
    private void verificaFilmeAdicionado(){

        //Detertmina a query
        Query mQuery = FirebaseDatabase.getInstance().getReference().child("Usuarios").
                child(mFirebaseAuth.getCurrentUser().getUid()).child("FilmesFavoritos").
                orderByChild("Titulo").equalTo(filmeFavorito.getText().toString());


        //inicia a query
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) { // o filme já foi adicionado anteriormente

                    Toast.makeText(getActivity(), R.string.movie_alredy_exists, Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();

                }else {//filme não adicionado - apto a salvar os dados


                    salvarDadosFilme();//salva os dados no banco de dados


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {//erro

                //mensagem de alerta
                //Toast.makeText(getActivity(), R.string.server_error, Toast.LENGTH_SHORT).show();


            }
        });
    }

    //funcao para salvar os dados no bd
    private void salvarDadosFilme (){

        //referencia o bd
        DatabaseReference novoFilme = databaseReference.child("Usuarios").child(mFirebaseAuth.getCurrentUser().getUid()).child("FilmesFavoritos").push().child("Titulo");

        //inicia o processo de salvar
        novoFilme.setValue(filmeFavorito.getText().toString(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {//ocorreu um erro no envio

                    Log.i("MovieSent", "Data could not be saved " + databaseError.getMessage());

                    //esconde o dialogo de carregamento
                    mProgressDialog.dismiss();

                    hideKeyboard();// Esconde o teclado

                    //mensagem de alerta
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_movie_sent),Toast.LENGTH_LONG).show();

                } else {//filme favorito enviado

                    Log.i("MovieSent", "Movie saved successfully");

                    //esconde o dialogo de carregamento
                    mProgressDialog.dismiss();

                    filmeFavorito.setText("");//Limpa o editText

                    hideKeyboard();// Esconde o teclado

                    //mensagem de alerta
                    Toast.makeText(getActivity(), getResources().getString(R.string.movie_sent),Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    //funcao para esconder o teclado
    private void hideKeyboard() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
