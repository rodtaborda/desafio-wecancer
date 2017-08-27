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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import rodrigotaborda.desafiowecancer.R;


public class LoginFragment extends Fragment {

    //Criando atributos
    private EditText emailLogar;
    private EditText senhaLogar;
    private Button botaoEntrar;
    private TextView textViewCadastrar;
    private TextView textViewEsqueceuSenha;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog mProgressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        //Referenciando objetos
        emailLogar = (EditText) view.findViewById(R.id.editTextEmailLogar);
        senhaLogar = (EditText) view.findViewById(R.id.editTextSenhaLogar);
        botaoEntrar = (Button) view.findViewById(R.id.botaoEntrar);
        textViewCadastrar = (TextView) view.findViewById(R.id.textViewCadastrar);
        textViewEsqueceuSenha = (TextView) view.findViewById(R.id.textViewEsqueciSenha);
        mProgressDialog = new ProgressDialog(getActivity());

        //Instanciando firebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        //verifica se o usuário já fez o login
        checharUsuarioLogado();



        //Detecta clique na tela
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                esconderTeclado(); //esconde o teclado

            }
        });




        //Detecta clique no textView
        textViewCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                irFragmentoRegistro();//vai para tela de cadastro

            }
        });


        //Detecta clique no TextView
        textViewEsqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //verificar se o usuário inseriu o email

                if (!emailLogar.getText().toString().equals("")){

                    enviarEmailAlteracaoSenha ();//Solicita recuperacao de senha

                }else{//campo vazio

                    Toast.makeText(getActivity(), R.string.email_password_reset_empty,Toast.LENGTH_LONG).show();//Mensagem de alerta
                }


            }
        });


        //Detecta clique no botão
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //esconde o teclado
                esconderTeclado();

                //verificar se os campos foram preenchidos
                if (emailLogar.getText().toString().equals("")
                        || senhaLogar.getText().toString().equals("")) {//um dos campos está em branco

                    //mensagem de alerta
                    Toast.makeText(getActivity(), R.string.email_password_empty, Toast.LENGTH_SHORT).show();


                } else {

                    //Mostra o diálogo de progresso
                    mProgressDialog.setMessage(getResources().getString(R.string.loading));
                    mProgressDialog.show();

                    //inicia o login do usuário
                    mFirebaseAuth.signInWithEmailAndPassword(emailLogar.getText().toString(), senhaLogar.getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {//sucesso ao logar usuario

                                        Log.i("loginUser", "Usuário logado com sucesso");

                                        //esconder o dialogo de carregamento
                                        mProgressDialog.dismiss();

                                        irFragmentoPrincipal();//ir para a tela inicial

                                    } else {//erro ao logar usuario

                                        //esconder o dialogo de carregamento
                                        mProgressDialog.dismiss();

                                        Log.i("loginUser", "Erro ao logar!" + task.getException());

                                        //Busca a mensagem de erro
                                        String exception =((FirebaseAuthException)task.getException()).getErrorCode();

                                        //compara as mensagens de erro para comunicao ao usuário
                                        switch (exception) {
                                            case "ERROR_INVALID_EMAIL":

                                                //Mensagem de alerta
                                                Toast.makeText(getActivity(), R.string.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show();
                                                break;

                                            case "ERROR_USER_NOT_FOUND":

                                                //Mensagem de alerta
                                                Toast.makeText(getActivity(), R.string.ERROR_USER_NOT_FOUND, Toast.LENGTH_LONG).show();
                                                break;

                                            case "ERROR_WRONG_PASSWORD":

                                                //Mensagem de alerta
                                                Toast.makeText(getActivity(), R.string.ERROR_WRONG_PASSWORD, Toast.LENGTH_LONG).show();
                                                break;
                                        }

                                    }
                                }
                            });
                }

            }

        });



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        checharUsuarioLogado();
    }

    //funcao para verificar usuario logado
    private void checharUsuarioLogado(){


        //Verificando se o usuário está logado
        if (mFirebaseAuth.getCurrentUser() != null){
            irFragmentoPrincipal();//vai para a tela inicial
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


    //funcao para alterar fragmentos
    private void irFragmentoRegistro (){

        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        RegistroFragment registerFragment = new RegistroFragment();

        fragmentTransaction.replace(R.id.fragment_container, registerFragment);
        fragmentTransaction.addToBackStack(null).commit();
    }


    //funcao para alterar fragmentos
    private void irFragmentoPrincipal(){

        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MainFragment registerFragment = new MainFragment();

        fragmentTransaction.replace(R.id.fragment_container, registerFragment).commit();


    }



    //funcao para envio de email de recuperacao de senha
    private void enviarEmailAlteracaoSenha(){

        //solicita o envio do email
        mFirebaseAuth.sendPasswordResetEmail(emailLogar.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {//email enviado

                Log.i("ResetPasswordEmail", "Mail sent");

                //esconde o dialogo de carregamento
                mProgressDialog.dismiss();

                //comunica o usuário
                Toast.makeText(getActivity(), R.string.change_password_message, Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {//erro no envio do email
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.i("ResetPasswordEmail", "Erro ao logar!" + e.getLocalizedMessage());

                //esconde o dialogo de carregamento
                mProgressDialog.dismiss();

                //Busca a mensagem de erro
                String exception =(e.getLocalizedMessage());


                switch (exception) {//compara os erros para mostrar mensagem correta ao usuário

                    case "An internal error has occurred. [ INVALID_EMAIL ]":

                        Toast.makeText(getActivity(), R.string.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show();
                        break;

                    case "There is no user record corresponding to this identifier. The user may have been deleted.":

                        Toast.makeText(getActivity(), R.string.ERROR_USER_NOT_FOUND, Toast.LENGTH_LONG).show();
                        break;

                    default:

                        Toast.makeText(getActivity(), R.string.unindentified_error, Toast.LENGTH_LONG).show();
                        break;
                }


            }
        });


    }

}
