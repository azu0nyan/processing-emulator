package processingEmulator.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import processingEmulator.*;

public class ActionsTest {
    @Before
    public void init(){
        AddAccountActionCreator.createAndExecute(2);
        AddAccountActionCreator.createAndExecute(3);
        AccountsMap.getInstance().get(3).setMoney(10);
    }

    @Test(timeout = 1000)
    public void testAddAccount(){
        AccountsMap.getInstance().removeAccount(1);
        AddAccountActionCreator.createAndExecute(1);
        assertTrue("Account not exist", AccountsMap.getInstance().get(1) != null);
    }

    @Test(timeout = 1000)
    public void testAddRemoveAccount(){
        AddAccountActionCreator.createAndExecute(1);
        RemoveAccountActionCreator.createAndWaitExecution(1);
        assertTrue("Removed account exist", AccountsMap.getInstance().get(1) == null);
    }

    @Test(timeout = 1000)
     public void testAddMoney(){
        double d = AccountsMap.getInstance().get(2).getMoney();
        AddMoneyActionCreator.createAndWaitExecution(2, 100);
        assertTrue("Money not added", d + 100 == AccountsMap.getInstance().get(2).getMoney());
    }

    @Test(timeout = 1000)
    public void testRemoveMoney(){
        double d = AccountsMap.getInstance().get(2).getMoney();
        RemoveMoneyActionCreator.createAndWaitExecution(2, d + 1);
        assertTrue("A lot of money removed", d == AccountsMap.getInstance().get(2).getMoney());
    }

    @Test
    public void testTransferMoney(){
        double acc2 = AccountsMap.getInstance().get(2).getMoney();
        double acc3 = AccountsMap.getInstance().get(3).getMoney();
        TransferMoneyActionCreator.createAndWaitExecution(3, 2, 5);
        assertTrue("Wrong money transfer", acc2 + 5 == AccountsMap.getInstance().get(2).getMoney() &&
                acc3 - 5 == AccountsMap.getInstance().get(3).getMoney());
    }

    @After
    public void end(){

    }

}
