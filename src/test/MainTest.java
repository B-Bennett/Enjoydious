import com.instio.Concession;
import com.instio.Main;
import com.instio.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Created by BennettIronYard on 11/3/15.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        Main.createTables(conn);
        return conn;
    }
    public void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE users");
        //stmt.execute("DROP TABLE messages");
        conn.close();
    }
    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        endConnection(conn);

        assertTrue(user != null);
    }
    @Test
    public void testConcession() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        Main.insertConcession(conn, 1, "Hot Dog", "Hot Dog", 12);
        Concession concession = Main.selectConcession(conn, 1);
        endConnection(conn);
        assertTrue(concession != null);
    }
    /*@Test
    public void testChooseFood() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        Main.insertUser(conn, "Bob", "");
        Main.insertConcession(conn, 1, "Pizza", "Pizza", 5);
        Main.insertConcession(conn, 2, "Fries", "Fries", 3);
        Main.insertConcession(conn, 2, "Burger", "Burger", 2);


    }*/
}
