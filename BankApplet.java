import java.applet.Applet;
import java.awt.Graphics;

public class BankApplet extends Applet {

    String accountHolder;
    String bankName;

    public void init() {

        accountHolder = getParameter("holder");
        bankName = getParameter("bank");

        if (accountHolder == null) {
            accountHolder = "Not provided";
        }

        if (bankName == null) {
            bankName = "Not provided";
        }
    }

    public void start() {
        // Applet starts
    }

    public void paint(Graphics g) {

        g.drawString(
            "Account Holder: " + accountHolder, 20, 30);

        g.drawString(
            "Bank Name: " + bankName, 20, 60);
    }

    public void stop() {
        // Applet stops
    }

    public void destroy() {
        // Applet destroyed
    }
}