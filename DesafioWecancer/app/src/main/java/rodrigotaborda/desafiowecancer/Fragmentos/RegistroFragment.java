package rodrigotaborda.desafiowecancer.Fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rodrigotaborda.desafiowecancer.R;


public class RegistroFragment extends Fragment {

    //Criando atributos
    private EditText nomeCadastrar;
    private EditText sobrenomeCadastar;
    private EditText enderecoCadastrar;
    private EditText cepCadastrar;
    private EditText emailCadastrar;
    private EditText senhaCadastrar;
    private EditText senhaConfirmar;
    private Button botaoCadastrar;
    private TextView textViewFragmentoLogin;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        //Referenciando objetos
        nomeCadastrar = (EditText) view.findViewById(R.id.editTextNomeCadastrar);
        sobrenomeCadastar = (EditText) view.findViewById(R.id.editTextSobrenomeCadastrar);
        enderecoCadastrar = (EditText) view.findViewById(R.id.editTextEnderecoCadastrar);
        cepCadastrar = (EditText) view.findViewById(R.id.editTextCepCadastrar);
        emailCadastrar = (EditText) view.findViewById(R.id.editTextEmailCadastrar);
        senhaCadastrar = (EditText) view.findViewById(R.id.editTextSenhaCadastrar);
        senhaConfirmar = (EditText) view.findViewById(R.id.editTextConfirmarSenha);
        botaoCadastrar = (Button) view.findViewById(R.id.buttonCadastrar);
        textViewFragmentoLogin = (TextView) view.findViewById((R.id.textViewLogar));
        mProgressDialog = new ProgressDialog(getActivity());

        //Instancia FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        //Instancia FirebaseDatabase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Detecta o click no textView
        textViewFragmentoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                irFragmentoLogin();//vai para a tela de login

            }
        });


        //Detecta o click no botão
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registrarUsuario();//cadastra o usuário

            }
        });

        //Detecta clique na tela
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                esconderTeclado();//esconde o teclado

            }
        });

        //adiciona a máscara de cep ao edit text
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNNNN" + "-" + "NNN");
        MaskTextWatcher mtw = new MaskTextWatcher(cepCadastrar, smf);
        cepCadastrar.addTextChangedListener(mtw);


        return view;
    }


    //funcao para alterar fragmentos
    private void irFragmentoLogin(){

        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LoginFragment loginFragment = new LoginFragment();

        fragmentTransaction.add(R.id.fragment_container, loginFragment);
        fragmentTransaction.addToBackStack(null).commit();

    }



    //inicia o registro de usuário
    private void registrarUsuario(){

        //verificar se todos os campos foram preenchidos
        if (nomeCadastrar.getText().toString().equals("")
                || sobrenomeCadastar.getText().toString().equals("")
                || enderecoCadastrar.getText().toString().equals("")
                || cepCadastrar.getText().toString().equals("")
                || emailCadastrar.getText().toString().equals("")
                || senhaCadastrar.getText().toString().equals("")
                || senhaConfirmar.getText().toString().equals("")){


            //Mensagem de alerta
            Toast.makeText(getActivity(), R.string.email_password_empty, Toast.LENGTH_SHORT).show();

        } else {

            //Verifica o formato do cep
            if (cepCadastrar.getText().length() == 9){//cep em formato correto


                //verifica se as senhas são idênticas
                if (senhaCadastrar.getText().toString().equals(senhaConfirmar.getText().toString())){//senhas idênticas

                    //mostra o dialogo de carregamento
                    mProgressDialog.setMessage(getResources().getString(R.string.loading));
                    mProgressDialog.show();


                    //inicia a criação do usuário
                    mFirebaseAuth.createUserWithEmailAndPassword(emailCadastrar.getText().toString(), senhaCadastrar.getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {//sucesso ao cadastrar usuario

                                        salvarDados();//Salva os demais dados do usuário (nome, sobrenome, endereço e cep)


                                    } else {//erro ao cadastrar usuario

                                        Log.i("createUser", "Erro no cadastro" + task.getException().toString() + task.getException());

                                        //Esconde o diálogo de carregamento
                                        mProgressDialog.dismiss();


                                        //Busca a mensagem de erro
                                        String exception = ((FirebaseAuthException) task.getException()).getErrorCode();

                                        //compara a mensagem de erro para alertar o usuário
                                        switch (exception) {
                                            case "ERROR_INVALID_EMAIL":

                                                //mensagem de alerta
                                                Toast.makeText(getActivity(), R.string.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show();
                                                break;

                                            case "ERROR_EMAIL_ALREADY_IN_USE":

                                                //mensagem de alerta
                                                Toast.makeText(getActivity(), R.string.ERROR_EMAIL_ALREADY_IN_USE, Toast.LENGTH_LONG).show();
                                                break;

                                            case "ERROR_WEAK_PASSWORD":

                                                //mensagem de alerta
                                                Toast.makeText(getActivity(), R.string.ERROR_WEAK_PASSWORD, Toast.LENGTH_LONG).show();
                                                break;
                                        }



                                    }
                                }
                            });

                }else{//senhas diferentes



                    //mensagem de alerta
                    Toast.makeText(getActivity(), R.string.different_password, Toast.LENGTH_LONG).show();


                }


            } else{//cep em formato incorreto

                //mensagem de alerta
                Toast.makeText(getActivity(), R.string.zipcode_format, Toast.LENGTH_LONG).show();


            }


        }




    }

    //funcao para alterar fragmentos
    private void irTelaInicial(){

        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LoginFragment loginFragment = new LoginFragment();

        fragmentTransaction.replace(R.id.fragment_container, loginFragment);
        fragmentTransaction.addToBackStack(null).commit();
    }


    //funcao para salvar os dados do usuário (nome, sobrenome, endereco e cep)
    private void salvarDados(){

        //mostra o caminho do nó no banco de dados
        DatabaseReference dados = mDatabaseReference.child("Usuarios").child(mFirebaseAuth.getCurrentUser().getUid()).child("Dados");

        //insere os dados
        dados.child("nome").setValue(nomeCadastrar.getText().toString());
        dados.child("sobrenome").setValue(sobrenomeCadastar.getText().toString());
        dados.child("endereco").setValue(enderecoCadastrar.getText().toString());

        //verifica se os dados foram salvos
        dados.child("cep").setValue(cepCadastrar.getText().toString(),new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {//ocorreu um erro no envio

                    Log.i("DataSaved", "Data could not be saved " + databaseError.getMessage());

                    //esconde o dialogo de carregamento
                    mProgressDialog.dismiss();

                    esconderTeclado(); //esconde o teclado

                    //caso tenha ocorrido um erro ao salvar os dados, deleta o usuário e reinicia o processo
                    mFirebaseAuth.getCurrentUser().delete();

                    //mensagem de alerta
                    Toast.makeText(getActivity(), R.string.ERROR_REGISTER_ACCOUNT, Toast.LENGTH_LONG).show();


                } else {//Dados do usuário salvos

                    Log.i("DataSaved", "Data saved successfully");

                    //esconde o dialogo de carregamento
                    mProgressDialog.dismiss();

                    irTelaInicial();// vai para a tela inicial

                    esconderTeclado(); //esconde o teclado

                    //enviar email de confirmação
                    enviarEmailVerificação();

                    //mensagem de boas vindas
                    Toast.makeText(getActivity(), R.string.welcome_message, Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    //funcao para envio de email de confirmacao
    private void enviarEmailVerificação(){

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        //verifica se existe usuário logado
        if (usuario != null) {//usuário logado
            usuario.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {//envia o email
                @Override
                public void onComplete(@NonNull Task<Void> task) {//email enviado
                    if (task.isSuccessful()) {

                        Log.i("ConfirmationEmail", "Email sent");

                    } else {//erro ao enviar email

                        Log.i("ConfirmationEmail", "Error on email verification" );
                    }

                }
            });
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
