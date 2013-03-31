package processingEmulator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DisplayAccounts {
    public List<SimpleAccount> getAccounts(){
       return AccountsMap.getInstance().getSimpleAccounts();
    }
}
