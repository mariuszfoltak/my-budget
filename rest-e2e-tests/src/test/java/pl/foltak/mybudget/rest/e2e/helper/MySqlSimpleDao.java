package pl.foltak.mybudget.rest.e2e.helper;

import java.util.Date;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class MySqlSimpleDao implements SimpleDao {

    private final JdbcTemplate jdbcTemplate;

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
    }

    @Override
    public Long createUser(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("username", username)
                .addValue("passwordHash", hashedPassword);

        return new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(parameters).longValue();
    }

    @Override
    public void clearAllTables() {
        jdbcTemplate.execute("delete from transactions");
        jdbcTemplate.execute("delete from categories");
        jdbcTemplate.execute("delete from accounts");
        jdbcTemplate.execute("delete from users");
    }

    @Override
    public Long createAccount(Long userId, String accountName) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", accountName)
                .addValue("user_id", userId);

        return new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("accounts")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(parameters)
                .longValue();
    }

    @Override
    public Long getUserId(String username) {
        final String sql = "Select id from users where username = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, username);
    }

    @Override
    public Long getAccountId(Long userId, String accountName) {
        final String sql = "Select id from accounts where user_id = ? and name = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, userId, accountName);
    }

    @Override
    public Long createCategory(Long userId, String categoryName) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", categoryName)
                .addValue("user_id", userId);

        return new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("categories")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(parameters)
                .longValue();
    }

    @Override
    public Long createTransaction(Long accountId, Long categoryId, String description, Double amount, Date date) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("account_id", accountId)
                .addValue("category_id", categoryId)
                .addValue("description", description)
                .addValue("transaction_date", date)
                .addValue("amount", amount);

        return new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("transactions")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(parameters)
                .longValue();
    }
}
