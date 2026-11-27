import java.util.InputMismatchException;
import java.util.Scanner;

// User-defined checked exception
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

// Bank Account class
class BankAccount {
    private double balance;

    public BankAccount(double balance) {
        this.balance = balance;
    }

    // Deposit method
    public synchronized void deposit(double amount) {
        if (amount > 0) {
            balance += amount;

            System.out.println("Deposited: MWK " + amount);
            System.out.println("Balance: MWK " + balance);

            notifyAll();
        }
    }

    // Withdrawal method
    public synchronized void withdraw(double amount)
            throws InsufficientFundsException,
            InterruptedException {

        if (amount > 500000) {
            throw new InsufficientFundsException(
                "Withdrawal exceeds MWK 500,000");
        }

        while (balance < amount) {
            System.out.println("Insufficient balance. Waiting...");
            wait();
        }

        balance -= amount;

        System.out.println("Withdrawn: MWK " + amount);
        System.out.println("Balance: MWK " + balance);
    }

    public synchronized double getBalance() {
        return balance;
    }
}

// Depositor thread
class Depositor extends Thread {
    private BankAccount account;

    public Depositor(BankAccount account) {
        this.account = account;
    }

    public void run() {
        for (int i = 1; i <= 5; i++) {
            account.deposit(50000);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}

// Withdrawer using Runnable
class Withdrawer implements Runnable {
    private BankAccount account;

    public Withdrawer(BankAccount account) {
        this.account = account;
    }

    public void run() {
        for (int i = 1; i <= 5; i++) {
            try {
                account.withdraw(60000);
                Thread.sleep(500);

            } catch (InsufficientFundsException e) {
                System.out.println(e.getMessage());
                return;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}

// Main class
public class BankAccountSystem {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        try {
            System.out.print("Enter account holder name: ");
            String name = input.nextLine();

            System.out.print("Enter opening balance: ");
            double openingBalance = input.nextDouble();

            if (openingBalance < 0) {
                System.out.println("Invalid opening balance.");
                return;
            }

            BankAccount account =
                new BankAccount(openingBalance);

            Depositor depositor = new Depositor(account);

            Thread withdrawer =
                new Thread(new Withdrawer(account));

            // Thread priorities
            depositor.setPriority(Thread.MAX_PRIORITY);
            withdrawer.setPriority(Thread.MIN_PRIORITY);

            System.out.println("\nAccount holder: " + name);

            depositor.start();
            withdrawer.start();

            // Wait for both threads
            depositor.join();
            withdrawer.join();

            System.out.println("\nTransactions completed.");
            System.out.println("Final balance: MWK "
                    + account.getBalance());

        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Program interrupted.");

        } finally {
            input.close();
            System.out.println("Bank program closed.");
        }
    }
}