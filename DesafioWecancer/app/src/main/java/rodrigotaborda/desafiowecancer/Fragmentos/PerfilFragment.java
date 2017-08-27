package rodrigotaborda.desafiowecancer.Fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import rodrigotaborda.desafiowecancer.R;


public class PerfilFragment extends Fragment {


    //cria atributos
    private Button botaoCancelar;
    private Button botaoSalvar;
    private EditText nomeAlterar;
    private EditText sobrenomeAlterar;
    private EditText enderecoAlterar;
    private EditText cepAlterar;
    private EditText emailAlterar;
    private ImageButton botaoEditar;
    private Button alterarSenha;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //referencia objetos
        botaoCancelar = (Button) view.findViewById(R.id.buttonCancelar);
        nomeAlterar = (EditText) view.findViewById(R.id.editTextNomeAlterar);
        sobrenomeAlterar = (EditText) view.findViewById(R.id.editTextSobrenomeAlterar);
        enderecoAlterar = (EditText) view.findViewById(R.id.editTextEnderecoAlterar);
        cepAlterar = (EditText) view.findViewById(R.id.editTextCepAlterar);
        emailAlterar = (EditText) view.findViewById(R.id.editTextEmailAlterar);
        botaoEditar = (ImageButton) view.findViewById(R.id.buttonEditar);
        botaoSalvar = (Button) view.findViewById(R.id.buttonSalvar);
        alterarSenha = (Button) view.findViewById(R.id.textViewAlterarSenha);
        mProgressDialog = new ProgressDialog(getActivity());

        //instancia o FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        //instancia o FirebaseDatabase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(mFirebaseAuth.getCurrentUser().getUid()).child("Dados");


        //impossibilita a ediçao das caixas de texto
        editTextNaoEditavel();

        //deixa o botao editar invisivel
        botaoSalvar.setVisibility(View.INVISIBLE);
        botaoCancelar.setVisibility(View.INVISIBLE);
        alterarSenha.setVisibility(View.VISIBLE);

        //adiciona a mascara de cep ao edit text
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNNNN" + "-" + "NNN");
        MaskTextWatcher mtw = new MaskTextWatcher(cepAlterar, smf);
        cepAlterar.addTextChangedListener(mtw);

        carregarDados();//carrega os dados do usuário


        //reconhece click do botao
        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //troca visibilidade dos botoes
                botaoEditar.setVisibility(View.INVISIBLE);
                botaoSalvar.setVisibility(View.VISIBLE);
                botaoCancelar.setVisibility(View.VISIBLE);
                alterarSenha.setVisibility(View.INVISIBLE);

                editTextEditavel();//torna o edittext editável


            }
        });


        //reconhece click do botao
        botaoCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editTextNaoEditavel();//nao permite a edicao de edittext

                //troca visibilidade dos botoes
                botaoEditar.setVisibility(View.VISIBLE);
                botaoSalvar.setVisibility(View.INVISIBLE);
                botaoCancelar.setVisibility(View.INVISIBLE);
                alterarSenha.setVisibility(View.VISIBLE);

                carregarDados();//carrega os dados do usuario



            }
        });


        //reconhece click do botao
        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mostra o diálogo de carregamento
                mProgressDialog.setMessage(getResources().getString(R.string.loading));
                mProgressDialog.show();

                atualizarDados();//atualiza os dados no bd
            }
        });

        //reconhece click do botao
        alterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mostra o diálogo de carregamento
                mProgressDialog.setMessage(getResources().getString(R.string.loading));
                mProgressDialog.show();

                //envia email de alteracao de senha
                enviarEmailAlteracaoSenha();

            }
        });


        //esconde o teclado com o click na tela
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

            }
        });


        return view;
    }

    //funcao para carregar os dados do usuário
    private void carregarDados(){

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Acessa os dados do BD
                String nome = (String) dataSnapshot.child("nome").getValue();
                String sobrenome = (String) dataSnapshot.child("sobrenome").getValue();
                String endereco = (String) dataSnapshot.child("endereco").getValue();
                String cep = (String) dataSnapshot.child("cep").getValue();
                String email = mFirebaseAuth.getCurrentUser().getEmail();

                //Carrega os dados nos campos (ET)
                nomeAlterar.setText(nome);
                sobrenomeAlterar.setText(sobrenome);
                enderecoAlterar.setText(endereco);
                cepAlterar.setText(cep);
                emailAlterar.setText(email);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {//erro no servidor

                //Toast.makeText(getActivity(), R.string.server_error, Toast.LENGTH_SHORT).show();


            }

        });


    }

    private void editTextNaoEditavel(){//Impossibilitar edição dos dados

        nomeAlterar.setEnabled(false);
        nomeAlterar.setClickable(false);

        sobrenomeAlterar.setEnabled(false);
        sobrenomeAlterar.setClickable(false);

        enderecoAlterar.setEnabled(false);
        enderecoAlterar.setClickable(false);

        cepAlterar.setEnabled(false);
        cepAlterar.setClickable(false);

        emailAlterar.setEnabled(false);
        emailAlterar.setClickable(false);

    }

    private void editTextEditavel(){//possibilitar edicao dos dados


        nomeAlterar.setEnabled(true);
        nomeAlterar.setClickable(true);

        sobrenomeAlterar.setEnabled(true);
        sobrenomeAlterar.setClickable(true);

        enderecoAlterar.setEnabled(true);
        enderecoAlterar.setClickable(true);

        cepAlterar.setEnabled(true);
        cepAlterar.setClickable(true);

        emailAlterar.setEnabled(true);
        emailAlterar.setClickable(true);


    }

    //funcao para atualizar os dados no firebase
    private void atualizarDados(){

        //verifica se todos os campos estao preenchidos
        if (nomeAlterar.getText().toString().equals("")
                || sobrenomeAlterar.getText().toString().equals("")
                || enderecoAlterar.getText().toString().equals("")
                || cepAlterar.getText().toString().equals("")) {//algum campo em branco


            //mensagem de alerta
            Toast.makeText(getActivity(), R.string.email_password_empty, Toast.LENGTH_SHORT).show();

        } else {//todos os campos preenchidos

            //verificar formato do cep
            if (cepAlterar.getText().length() == 9) {//cep em formato correto

                //verifica se o email foi editado pelo usuário - caso sim, altera no Firebase
                if (!emailAlterar.getText().toString().equals(mFirebaseAuth.getCurrentUser().getEmail())) {//email diferente

                    //altera o email
                    mFirebaseAuth.getCurrentUser().updateEmail(emailAlterar.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {//email alterado


                            salvarDados();//alterar os outros dados


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {//erro ao alterar o email

                            //esconde o dialogo de carregamento
                            mProgressDialog.dismiss();

                            //carrega os dados antigos
                            carregarDados();

                            //compara as mensagens de erro para informar o usuario
                            if (e.getMessage().equals("The email address is already in use by another account.") ) {

                                Toast.makeText(getActivity(), R.string.ERROR_EMAIL_ALREADY_IN_USE, Toast.LENGTH_LONG).show();


                            } else if (e.getMessage().equals("The email address is badly formatted.")) {

                                Toast.makeText(getActivity(), R.string.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show();


                            } else if (e.getMessage().equals("This operation is sensitive and requires recent authentication. Log in again before retrying this request.")) {

                                Toast.makeText(getActivity(), R.string.sensitive_operation_error, Toast.LENGTH_LONG).show();


                            }
                        }
                    });

                } else {//não foi editado

                    salvarDados();//alterar os outros dados do usuario
                }
            } else {//cep em formato incorreto

                //mensagem de alerta
                Toast.makeText(getActivity(), R.string.zipcode_format, Toast.LENGTH_LONG).show();

                //esconde o dialogo de carregamento
                mProgressDialog.dismiss();

            }
        }

    }


    private void salvarDados (){

        mDatabaseReference.child("nome").setValue(nomeAlterar.getText().toString());
        mDatabaseReference.child("sobrenome").setValue(sobrenomeAlterar.getText().toString());
        mDatabaseReference.child("endereco").setValue(enderecoAlterar.getText().toString());
        mDatabaseReference.child("cep").setValue(cepAlterar.getText().toString(),new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {//ocorreu um erro no envio

                    Log.i("DataUpdated", "Data could not be updated " + databaseError.getMessage());

                    mProgressDialog.dismiss();

                    Toast.makeText(getActivity(), R.string.user_data_updated_error, Toast.LENGTH_LONG).show();


                } else {//filme favorito enviado

                    Log.i("DataUpdated", "Data updated successfully");

                    mProgressDialog.dismiss();

                    editTextNaoEditavel();

                    //caso a operação seja concluida, esconder os botoes salvar e cancelar. Mostrar o editar e alterar senha.
                    botaoEditar.setVisibility(View.VISIBLE);
                    botaoSalvar.setVisibility(View.INVISIBLE);
                    botaoCancelar.setVisibility(View.INVISIBLE);
                    alterarSenha.setVisibility(View.VISIBLE);

                    Toast.makeText(getActivity(), R.string.user_data_updated, Toast.LENGTH_LONG).show();

                }
            }
        });
    }



    protected void enviarEmailAlteracaoSenha(){

        //abre o diálogo de confirmacao para alertar o usuário
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.change_password_confirmation))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //envia o email
                        mFirebaseAuth.sendPasswordResetEmail(mFirebaseAuth.getCurrentUser().getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void result) {

                                //email enviado
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), R.string.change_password_message, Toast.LENGTH_LONG).show();


                            }
                        }).addOnFailureListener(new OnFailureListener() { //erro no envio
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), R.string.change_password_error_message, Toast.LENGTH_LONG).show();

                            }
                        });


                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mProgressDialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

    public void hideKeyboard() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }









}
