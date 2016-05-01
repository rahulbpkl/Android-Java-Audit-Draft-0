public class Vector1 {}

public class Vector {
  private final int balance;
  private static final Vector c = new Vector();
  private static final int deposit = (int) (Math.random() * 100); // Random deposit
 
  public Vector() {
    balance = deposit - 10; // Subtract processing fee
  }
 
}

  public static void main(String[] args) {
    System.out.println("The account balance is: " + c.balance);
  }
}
