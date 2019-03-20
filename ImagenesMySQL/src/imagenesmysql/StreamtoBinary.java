/**Clase para convertir y guardar una imagen en mysql.
*
*/

package imagenesmysql;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StreamtoBinary {

/**Este método se emplea para guardar una imagen en una base de datos de Mysql
* @param args
* @author Ricardo Presilla
*/
public void Funcion(String[] args) {
System.out.println("reading inputstream");
Statement stmt = null;
Connection con = null;
    try {
        File file = new File("srcfilepath");
        FileInputStream iStream = new FileInputStream(file);
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/test";
        con = DriverManager.getConnection(url, "root", "");//Usuario y clave de acceso al MySQL
        stmt = con.createStatement();
        String sql = "insert into stb values(?,?)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, 1);
        pstmt.setBinaryStream(2, iStream, (int) file.length());
        // pstmt.executeUpdate();
        // con.commit();
        System.out.println("Escribiendo en la tabla:" + (int) file.length());
        // now read the blob object and write to a file
        sql = "SELECT id, doc FROM stb";
        PreparedStatement stmt1 = con.prepareStatement(sql);
        ResultSet resultSet = stmt1.executeQuery();
        int i = 0;
        while (resultSet.next()) {
            String id = resultSet.getString(1);
            File doc = new File("destfilepath");
            FileOutputStream fos = new FileOutputStream(doc);
            Blob b = resultSet.getBlob(2);
            BufferedOutputStream os;
            os = new BufferedOutputStream(new FileOutputStream(doc));
            os.write(b.getBytes(1, (int) b.length()), 0, (int) b.length());
            os.flush();
            os.close();
            System.out.println("Escribiendo el registro:" + id);
            System.out.println("Escribiendo el archivo:" + doc.length());
            // }
            fos.close();
            i++;
        }
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    } finally {
                        try {
                        stmt.close();
                        con.close();
                        } catch (SQLException e) {
                        e.printStackTrace();
                        }

                    }
}

}