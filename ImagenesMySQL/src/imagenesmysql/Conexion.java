/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imagenesmysql;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**Clase de conexión a la base de datos, guarda y lee los datos en la base de 
 * datos.
 * @author Ingeniro en Computación: Ricardo Presilla
 * @version 1.0.
 */
public class Conexion {
    public boolean band;
    private Connection conexion;
    private Statement sentencia;
    private ResultSet contenedor;
/**Constructor de la clase conexion. Aqui se establece la conexion con la base 
 * de datos. Se registra el JDBC, se verifican los datos de configuracion del 
 * sistema, se crea el objeto de conexion y se establece el nombre de la base 
 * de datos ha utilizar.
*/
    public Conexion() throws SQLException {
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String servidor="localhost";
            try{
                File Archivo = new File("config.txt");
                if(Archivo.exists()){
                    //Leyendo el archivo
                    FileInputStream f1 = new FileInputStream(Archivo);
                    InputStreamReader f2= new InputStreamReader(f1);
                    BufferedReader linea= new BufferedReader(f2);
                    servidor=linea.readLine();
                    linea.close();
                }
            }catch(IOException e){
                JOptionPane.showMessageDialog(null,"Error: En la configuracion del programa.", "Atención",JOptionPane.ERROR_MESSAGE);
            }
            conexion = DriverManager.getConnection("jdbc:mysql://"+servidor,"root","imagine.dragons.2017");
            sentencia = conexion.createStatement ();
            try{
                sentencia.executeQuery ("use PRUEBAS");
            }catch (Exception ex) {
                JOptionPane.showMessageDialog(null,"Error: Base de datos no encontrada.\n"+ex.getMessage(), "Atención",JOptionPane.ERROR_MESSAGE);
                band=false;
                return;
            }
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null,"Error en el controlador de la conexion.\n"+ex.getMessage(), "Atención",JOptionPane.ERROR_MESSAGE);
            sentencia.close ();
            conexion.close();
            System.exit(0);
        }
        band=true;
    }
/**Guarda una imagen en una base de datos.
* @param nombre Tipo String, contiene el nombre de la imagen.
* @param ruta Tipo String, contiene la ruta de la imagen a guardar.
* @return Regresa verdadero si realiza la operación, sino regresa falso.
*/
    public boolean GuardarImagen(String nombre, String ruta){
        try{
            String sql= "insert into imagenes (nombre, img) values (?, ?)";
            PreparedStatement instruccion= conexion.prepareStatement(sql);
            File imagen =new File(ruta);
            FileInputStream fis= new FileInputStream(imagen);
            instruccion.setString(1, nombre);
            instruccion.setBinaryStream(2, fis, (int) imagen.length());
            instruccion.execute();
            try {
                fis.close(); //Cierra el archivo usado
            } catch (IOException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }catch (SQLException | FileNotFoundException ex){
            JOptionPane.showMessageDialog(null,"Error en el controlador de la conexion.\n"+ex.getMessage(), "Atención",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
/**Guarda una imagen en una base de datos.
* @param nombre Tipo String, contiene el nombre de la imagen.
* @param archivo Tipo File, contiene la imagen a guardar.
* @return Regresa verdadero si realiza la operación, sino regresa falso.
*/
    public boolean GuardarImagen(String nombre, File archivo){
        try{
            String sql= "insert into imagenes (nombre, img) values (?, ?)";
            PreparedStatement instruccion= conexion.prepareStatement(sql);
            FileInputStream fis= new FileInputStream(archivo);
            instruccion.setString(1, nombre);
            instruccion.setBinaryStream(2, fis, (int) archivo.length());
            instruccion.execute();
            try {
                fis.close(); //Cierra el archivo usado
            } catch (IOException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }catch (SQLException | FileNotFoundException ex){
            JOptionPane.showMessageDialog(null,"Error en el controlador de la conexion.\n"+ex.getMessage(), "Atención",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
/**Lee una imagen de una base de datos y la regresa como variable.
* @return Regresa una imagen si existe, sino regresa nulo.
*/
    public Image LeerImagen(){
        Image imagen= null;
        try{
            contenedor=sentencia.executeQuery("select * from imagenes;");
            if(contenedor.next()){
                Blob blob = contenedor.getBlob("img");
                byte[] data= blob.getBytes(1, (int)blob.length());
                BufferedImage img= null;
                try{ 
                    img=ImageIO.read(new ByteArrayInputStream(data));
                }catch (IOException ex){
                    JOptionPane.showMessageDialog(null,"Error al leer la imagen.\n"+ex.getMessage(), "Atención",JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                if(img!=null){
                    imagen=img;
                }
                contenedor.close();
            }
        }catch (SQLException ex){
            JOptionPane.showMessageDialog(null,"Error al leer la imagen de la base de datos.\n"+ex.getMessage(), "Atención",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return imagen;
    }
/**Este método lee una imagen de la base de datos y la guarda en el directorio 
 * actual del programa.
 * @param nomb Tipo String, contiene el nombre de la imagen a buscar.
 * @return Regresa verdadero s realiza la operación sino regresa falso.
 */
    public boolean DescargarImagen(String nomb){
        Blob blob=null;
        String nombre="";
        try{
            contenedor=sentencia.executeQuery("select * from imagenes where nombre='"+nomb+"';");
            if(contenedor.next()){
                nombre= contenedor.getString("nombre");
                blob = contenedor.getBlob("img");
                byte[] data= blob.getBytes(1, (int)blob.length());
                try {
                    FileOutputStream salida= new FileOutputStream(nombre);
                    salida.write(data);
                    salida.close();
                } catch (Exception ex) {
                    Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error al guardar en disco duro el archivo: "+nombre);
                }
            }
        }catch (SQLException ex){
            JOptionPane.showMessageDialog(null,"Error al leer la imagen de la base de datos.\n"+ex.getMessage(), "Atención",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
/**Este método guarda un documento en la base de datos.
 * @param dir Tipo String, contiene la dirección completa del archivo a guardar.
 * @param nombre Tipo String, contiene el nombre del archivo a guardar.
 * @return Regresa verdadero si realiza la operación, sino regresa falso.
*/
    public boolean GuardarDocumento(String dir, String nombre){
        try{
            String sql= "insert into documentos (nombre, archivo) values ("+nombre+", ?)";
            PreparedStatement instruccion= conexion.prepareStatement(sql);
            File archivo =new File(dir);
            FileInputStream fis= new FileInputStream(archivo);
            instruccion.setBinaryStream(1, fis, (int) archivo.length());
            instruccion.execute();
        }catch (SQLException | FileNotFoundException ex){
            JOptionPane.showMessageDialog(null,"Error en el controlador de la conexion.\n"+ex.getMessage(), "Atención",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
/**Cierra la conexion con la base de datos.
 * @throws java.sql.SQLException Posibles errores de desconexion del MySQL.
 */
public void Cerrar_Coneccion() throws SQLException{
    sentencia.close ();
    conexion.close();
}
    
}
