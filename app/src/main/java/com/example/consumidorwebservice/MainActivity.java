package com.example.consumidorwebservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import WebService.Asynchtask;

public class MainActivity extends AppCompatActivity implements Asynchtask, View.OnClickListener{

    //Declaracion de variables VISTA XML
    private Button consultar;
    private Button consultarPorID;
    private Button insertar;
    private Button actualizar;
    private Button borrar;
    public static EditText identificador;
    public static EditText nombre;
    public static EditText direccion;
    public static TextView resultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidNetworking.initialize(getApplicationContext());
        //Inicializacion de variables
        consultar = (Button) findViewById(R.id.btnConsultar);
        consultarPorID = (Button) findViewById(R.id.btnConsultarID);
        insertar = (Button) findViewById(R.id.btnInsertar);
        actualizar = (Button) findViewById(R.id.btnActualizar);
        borrar = (Button) findViewById(R.id.btnBorrar);
        identificador = (EditText) findViewById(R.id.etIdentificador);
        nombre = (EditText) findViewById(R.id.etNombre);
        direccion = (EditText) findViewById(R.id.etDireccion);
        resultado = (TextView) findViewById(R.id.tvResultado);

        //Asignacion metodo OnClickListener a los botones

        consultar.setOnClickListener(this);
        consultarPorID.setOnClickListener(this);
        insertar.setOnClickListener(this);
        actualizar.setOnClickListener(this);
        borrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String id=identificador.getText().toString();
        String nombres= nombre.getText().toString();
        String direcciones= direccion.getText().toString();

        switch (v.getId()){
            case R.id.btnConsultar:
                Map<String, String> datos=new HashMap<>();
                datos.put("metodo","seleccionar_alumnos");
                JSONObject jsonObject=new JSONObject(datos);
                ListarAlumno(jsonObject);
                break;
            case R.id.btnConsultarID:
                if (validarCampos("1")){
                    Map<String, String> datos1=new HashMap<>();
                    datos1.put("id",id);
                    datos1.put("metodo","seleccionar_alumno");
                    JSONObject jsonObject1=new JSONObject(datos1);
                    ListarIDAlumno(jsonObject1);

                }else{
                    resultado.setText("Favor ingresar ID para realizar consulta");
                }
                break;
            case R.id.btnInsertar:
                if (validarCampos("2")){
                    Map<String, String> datos2=new HashMap<>();
                    datos2.put("nombre",nombres);
                    datos2.put("direccion",direcciones);
                    datos2.put("metodo","insertar_alumno");
                    JSONObject jsonObject2=new JSONObject(datos2);
                    insertarAlumno(jsonObject2);

                }else{
                    resultado.setText("Favor ingresar ID, nombre y direccion para insertar Alumno");
                }
                break;
            case R.id.btnActualizar:
                if (validarCampos("3")){
                    Map<String, String> datos3=new HashMap<>();
                    datos3.put("id",id);
                    datos3.put("nombre",nombres);
                    datos3.put("direccion",direcciones);
                    datos3.put("metodo","actualizar_alumno");
                    JSONObject jsonObject3=new JSONObject(datos3);
                    actualizarAlumno(jsonObject3);
                }else{
                    resultado.setText("Favor ingresar ID, nombre y direccion para actualizar datos del alumno");
                }
                break;
            case R.id.btnBorrar:
                if (validarCampos("4")){
                    Map<String, String> datos4=new HashMap<>();
                    datos4.put("id",id);
                    datos4.put("metodo","eliminar_alumno");
                    JSONObject jsonObject4=new JSONObject(datos4);
                    eliminarAlumno(jsonObject4);
                }else{
                    resultado.setText("Favor ingresar ID para borrar alumno, los demas campos no seran tomados enc uenta");
                }
                break;
            default:
                break;
        }
    }

    public void ListarAlumno(JSONObject jsonObject) {

            AndroidNetworking.post("https://practicauteq.herokuapp.com/central_post.php")
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //String data=response.getString("estado");
                                JSONArray jsonArray=response.getJSONArray("data");
                                for (int i=0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                    resultado.append("Nombre: "+jsonObject1.getString("nombre")+"   Direccion"+jsonObject1.getString("direccion")+"  Clave"+jsonObject1.getString("idalumno")+"\n");
                                }
                            }catch (JSONException e){
                                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();;
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
    }

    public void ListarIDAlumno(JSONObject jsonObject) {

        AndroidNetworking.post("https://practicauteq.herokuapp.com/central_post.php")
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String data=response.getString("estado");
                            if (data !=null){
                                JSONArray jsonArray=response.getJSONArray("data");
                                JSONObject jsonObject1=jsonArray.getJSONObject(0);
                                nombre.setText(jsonObject1.getString("nombre"));
                                direccion.setText(jsonObject1.getString("direccion"));
                            }else{
                                Toast.makeText(getApplicationContext(),"No existe el dato",Toast.LENGTH_LONG).show();;
                            }

                        }catch (JSONException e){
                            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    public void insertarAlumno(JSONObject jsonObject) {

        AndroidNetworking.post("https://practicauteq.herokuapp.com/central_post.php")
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String data=response.getString("estado");
                            if (data !=null ){
                                Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();;
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    public void actualizarAlumno(JSONObject jsonObject) {

        AndroidNetworking.post("https://practicauteq.herokuapp.com/central_post.php")
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String data=response.getString("estado");
                            if (data !=null ){
                                Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"No existe el id de ese usuario",Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
    public void eliminarAlumno(JSONObject jsonObject) {

        AndroidNetworking.post("https://practicauteq.herokuapp.com/central_post.php")
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String data=response.getString("estado");
                            if (data !=null ){
                                Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"No existe el id de ese usuario",Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();;
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
    public boolean validarCampos(String operacion){
        Boolean flag = false;

        if (operacion.equals("1")){
            if(!identificador.getText().toString().isEmpty()){
                flag = true;
            }
        }else if(operacion.equals("2")){
            if (!nombre.getText().toString().isEmpty() && !direccion.getText().toString().isEmpty()){
                flag = true;
            }
        }else if(operacion.equals("3")){
            if (!identificador.getText().toString().isEmpty() && !nombre.getText().toString().isEmpty() && !direccion.getText().toString().isEmpty()){
                flag = true;
            }
        }else if(operacion.equals("4")){
            if(!identificador.getText().toString().isEmpty()){
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public void processFinish(String result) throws JSONException {
        JSONObject jsonObject=new JSONObject(result);
        JSONArray dt=jsonObject.getJSONArray("data");
        JSONObject c=dt.getJSONObject(0);

        nombre.setText(c.getString("nombre"));
        direccion.setText(c.getString("direccion"));
        identificador.setText(c.getString("clave"));
    }
}