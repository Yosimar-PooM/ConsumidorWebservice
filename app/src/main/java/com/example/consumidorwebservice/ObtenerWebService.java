package com.example.consumidorwebservice;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ObtenerWebService extends AsyncTask<String, Void, String> {

    public ObtenerWebService(){

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        String cadena = strings[0];
        String regreso = "";

        if (strings[1].equals("0")){ //Consultar todos los alumnos
            StringBuilder result = retornarSB(cadena);

            if (result != null){
                regreso = leerJSON(result, "all");
            }else{
                regreso = "No se pudo realizar la operacion, contacte a soporte";
            }


        }else if (strings[1].equals("1")){  //Consulta por ID

            StringBuilder result = retornarSB(cadena);

            if (result != null){
                regreso = leerJSON(result, "single");
            }else{
                regreso = "No se pudo realizar la operacion, contacte a soporte";
            }

        }else if (strings[1].equals("2")){  //Insertar alumno

            regreso = funcionesRUD(cadena, "insertar");

        }else if (strings[1].equals("3")){  //Actualizar alumno

            regreso = funcionesRUD(cadena, "actualizar");

        }else if (strings[1].equals("4")){  //Borrar alumno

            regreso = funcionesRUD(cadena, "borrar");

        }

        return regreso;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(s);
        MainActivity.resultado.setText(s);
        MainActivity.identificador.setText(null);
        MainActivity.nombre.setText(null);
        MainActivity.direccion.setText(null);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    public static StringBuilder retornarSB (String cadena){    //
        URL url = null;
        String linea = "";
        StringBuilder result = null;
        boolean todoOK = false;
        try{
            url = new URL(cadena);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           // connection.setRequestProperty("User-Agent","Mozilla/5.0" +
           //         " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
            int respuesta = connection.getResponseCode();
            result = new StringBuilder();
            if (respuesta == HttpURLConnection.HTTP_OK){
                InputStream in = new BufferedInputStream(connection.getInputStream()); //Se prepara la cadena de entrada
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));  //Se introduce en un buffered reader

                /*
                   Ahora para utilizar el JSONObject es necesario un string, por tanto hay que convertir
                   El BufferedReader en una cadena String
                 */


                //A continuacion se lee lina por linea el buffered reader y se va asignando al StringBuilder
                while ((linea = reader.readLine()) != null){
                    result.append(linea);
                } //UNa vez finalizado tenemos un StringBuilder result que leera el JSONObject
                todoOK = true;  //Si realizo todos los pasos del try, esta variable pasara a true con el propito de validar que la operacion se realizo exitosamente
            }
            connection.disconnect();
        }catch (MalformedURLException e){
            e.printStackTrace();;
        }catch (IOException e){
            e.printStackTrace();
        }

        //Se borran datos contenidos en memoria para optimizar

        if (todoOK == false){
            return null;
        }else{
            return result;
        }

    }

    public static String leerJSON (StringBuilder result, String mode){

        String regreso = "";

        try{
            //Se crea objeto JSON para poder acceder a los atributos (campos) del objeto
            JSONObject respuestaJSON = new JSONObject(result.toString());
            String resultJSON = respuestaJSON.getString("estado"); //Estado es el nombre del campo en el JSON

            if (resultJSON.equals("1")){ //Hay alumnos para mostrar
                JSONArray alumnosJSON = null;

                if (mode.equals("all")){
                    alumnosJSON = respuestaJSON.getJSONArray("alumnos");
                    for (int i=0; i < alumnosJSON.length(); i++){ //Se recorre el ARRAY y se asigna a la variable String
                        regreso = regreso + alumnosJSON.getJSONObject(i).getString("idAlumno") + " " +
                                alumnosJSON.getJSONObject(i).getString("nombre") + " " +
                                alumnosJSON.getJSONObject(i).getString("direccion") + "\n";
                    }

                }else if (mode.equals("single")){
                    //alumnosJSON = respuestaJSON.getJSONArray("alumnos");
                    regreso = regreso + respuestaJSON.getJSONObject("alumno").getString("idAlumno") +  " " +
                            respuestaJSON.getJSONObject("alumno").getString("nombre") + " " +
                            respuestaJSON.getJSONObject("alumno").getString("direccion") + "\n";
                }
            }else{
                regreso = "No se encontraron alumnos en nuestra base de datos";
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        System.gc();

        if (regreso.equals("")){
            return "No se pudo realizar la operacion, favor contactar a soporte";
        }else{
            return regreso;
        }

    }

    public static String funcionesRUD(String cadena, String operacion){

        DataOutputStream printout;
        DataInputStream input;
        OutputStream os;
        JSONObject jsonParam = new JSONObject();
        JSONObject respuestaJSON = null;
        StringBuilder result = null;

        String linea = "";
        String resultJSON = "";
        String regreso = "";
        Boolean flag = false;

        try{
            URL url = new URL(cadena);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Habilita conexion de entrada y salida
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Accept","application/json");
            connection.connect();

            //Se le asignan parametros al JSONObject
            if (operacion.equals("borrar")){
                jsonParam.put("idalumno", MainActivity.identificador.getText().toString()); //Se crea objeto JSON con el parametro a enviar, ejemplo {"idalumno":"0"}
            }else if (operacion.equals("insertar")){
                jsonParam.put("nombre", MainActivity.nombre.getText().toString());
                jsonParam.put("direccion", MainActivity.direccion.getText().toString());
            }else if (operacion.equals("actualizar")){
                jsonParam.put("idalumno", MainActivity.identificador.getText().toString()); //Se crea objeto JSON con el parametro a enviar, ejemplo {"idalumno":"0"}
                jsonParam.put("nombre", MainActivity.nombre.getText().toString());
                jsonParam.put("direccion", MainActivity.direccion.getText().toString());
            }

            //Envio de parametros POST
            os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();

            //Se verifica si fue posible la conexion
            int respuesta = connection.getResponseCode();
            result = new StringBuilder();

            //Si fue posible se crea un buffered reader y lo que lee se asigna a result (StringBuilder)
            if (respuesta == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));  //Se introduce en un buffered reader


                //A continuacion se lee linea por linea el buffered reader y se va asignando al StringBuilder
                while ((linea = reader.readLine()) != null){
                    result.append(linea);
                } //UNa vez finalizado tenemos un StringBuilder result que leera el JSONObject

                //Se verifica si se realizo o no la operacion
                respuestaJSON = new JSONObject(result.toString());
                resultJSON = respuestaJSON.getString("estado");

                if (resultJSON.equals("1")){
                    regreso = "Operacion realizada exitosamente";
                }else if (resultJSON.equals("2")){
                    regreso = "No se pudo realizar la operacion, favor contactar a soporte tecnico";
                }
            }
            connection.disconnect(); //Se termina la conexion
            flag = true;    //Si realiza completamente el try
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }

        if (flag == true){
            //No code here
        }else if (flag == false){
            regreso = "No se pudo realizar la operacion, contacte a soporte tecnico";
        }
        respuestaJSON = null;
        jsonParam = null;
        result = null;
        System.gc();

        return regreso; //Devuelve una cadena indicando si la operacion fue exitosa o no

    }

}
