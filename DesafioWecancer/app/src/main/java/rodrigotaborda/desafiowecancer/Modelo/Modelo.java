package rodrigotaborda.desafiowecancer.Modelo;

import android.widget.Button;

/**
 * Created by RodrigoTaborda on 25/08/17.
 */
//classe modelo para o recyclerView adapter
public class Modelo {


    private String Titulo;


    public Modelo(){
    }

    public Modelo(String Titulo) {

        this.Titulo = Titulo;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        this.Titulo = titulo;
    }



}




