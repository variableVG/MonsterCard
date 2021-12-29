package DB;

import lombok.Data;

@Data

public class DbConfig {

    protected String dbHost = "localhost";
    protected String dbPort = "5432";
    protected String dbUser = "postgres";
    protected String dbPassword  = "Globuli48";
    protected String dbName = "monsterCard";
}
