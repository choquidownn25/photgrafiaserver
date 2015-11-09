package sample.gcm.com.photgrafiaserver.datos;

/**
 * Created by choqu_000 on 09/11/2015.
 */
public class Cedula {
    //Atributos
    private String cedula;
    //Constuctor
    public Cedula(String cedula){
        this.cedula=cedula;
    }

    //Encapsulamiento
    public String getCedula() {
        return cedula;
    }

    public String setCedula(String cedula) {
        this.cedula = cedula;
        return cedula;
    }


}
