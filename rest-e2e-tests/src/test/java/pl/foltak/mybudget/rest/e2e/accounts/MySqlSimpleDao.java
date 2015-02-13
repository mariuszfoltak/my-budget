package pl.foltak.mybudget.rest.e2e.accounts;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class MySqlSimpleDao implements SimpleDao {

    private JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public MySqlSimpleDao() {
        String url = "jdbc:mysql://localhost:3306/";
        String db = System.getenv("MYBUDGET_SCHEMA_NAME");
        String user = System.getenv("MYBUDGET_SCHEMA_USER");
        String pass = System.getenv("MYBUDGET_SCHEMA_PASS");

        db = (db == null) ? "mybudget" : db;
        user = (user == null) ? "mybudget" : user;
        pass = (pass == null) ? "mybudget" : pass;

        DriverManagerDataSource dataSource = new DriverManagerDataSource(url + db, user, pass);
        jdbcTemplate = new JdbcTemplate(dataSource);
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
    }

    @Override
    public void createUser(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("username", username)
                .addValue("passwordHash", hashedPassword);
        simpleJdbcInsert.withTableName("users").execute(parameters);
    }

    @Override
    public void clearAllTables() {
        jdbcTemplate.execute("delete from users");
    }
}
