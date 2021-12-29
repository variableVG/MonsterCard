package DB;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DbHandlerTest {
    DbHandler dbHandler;

    @BeforeEach
    void setup() {
        dbHandler = new DbHandler();
    }

    @Test
    void initDBTest() throws Exception{
        dbHandler.initDb();
    }
}
