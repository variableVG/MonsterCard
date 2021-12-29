package DB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class DbConnectionTest {
    DbConnection dbConnection;

    @BeforeEach
    void setup() {
        dbConnection = new DbConnection();
    }

    @Test
    void connectTest() throws Exception{
        dbConnection.getConnection();
    }
}
