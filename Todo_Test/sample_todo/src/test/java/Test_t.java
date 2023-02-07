import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

public class Test_t {
    @Test
    public void 회원가입_중복_테스트() {
        int v1 = 10;
        int v2 = 10;
        Assertions.assertEquals(v1,v2);
        //assertArrayEquals(a,b) : 배열 a와 b가 일치함을 확인
        //assertEquals(a,b) : a와 b의 값이 같은지 확인
        //assertSame(a,b) : 객체 a와 b가 같은 객체임을 확인
        //assertTrue(a) : a가 참인지 확인
        //assertNotNull(a) : a객체가 null이 아님을 확인
    }
    @Test
    @DisplayName("마리아디비연결")
    public void testConnection() throws Exception {
        Class.forName("org.mariadb.jdbc.Driver");
        Connection connection = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/sample_todo",
                "root",
                "1234");
        Assertions.assertNotNull(connection);
        connection.close();
    }
    @Test
    @DisplayName("히키라")
    public void testHikariCP() throws Exception{
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl("jdbc:mariadb://localhost:3306/sample_todo");
        config.setUsername("root");
        config.setPassword("1234");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource ds = new HikariDataSource(config);
        Connection connection = ds.getConnection();
        System.out.println(connection);

        connection.close();

    }
}

