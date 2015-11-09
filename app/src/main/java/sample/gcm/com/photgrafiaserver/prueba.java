package sample.gcm.com.photgrafiaserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by choqu_000 on 05/11/2015.
 */
public class prueba extends Activity  {

    //Atributos
    private Button envia;
    private EditText textCedula;
    private String DatoCedula;
    private String PasaDato;
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.entradasistema);
        textCedula= (EditText) findViewById(R.id.editTextCedula);
        //
        envia=(Button)findViewById(R.id.btnenviar);
        envia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("Cedula", textCedula.getText().toString());
                Intent intent = new Intent(prueba.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                }
        });


    }

}
