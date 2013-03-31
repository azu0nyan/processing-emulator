package processingEmulator;

public class SimpleAccount {
    public int id;
    public double money;

    public SimpleAccount(int id, double money) {
        this.id = id;
        this.money = money;
    }
    public int getId(){
        return id;
    }
    public  double getMoney(){
        return money;
    }

}
