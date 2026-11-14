import com.sharespace.menu.MainMenu;

import java.util.Scanner;

/**
 * ============================================================
 *            SHARESPACE — Campus Resource Sharing System
 * ============================================================
 *
 * Entry point. Creates a single Scanner shared across all menus
 * to avoid input-skipping bugs, then launches MainMenu.
 *
 * Run with:
 *   java -jar ShareSpace-1.0.0-jar-with-dependencies.jar
 *
 * Or from IDE: run this class directly.
 *
 * Prerequisites:
 *   1. MySQL running with database 'sharespace' created.
 *      (import database/sharespace.sql)
 *   2. Update credentials in DBConnection.java if needed.
 *
 * ============================================================
 */
public class Main {
    public static void main(String[] args) {
        // Single Scanner instance shared across all menus
        // Avoids Scanner resource leaks and input-skipping bugs
        Scanner scanner = new Scanner(System.in);

        printBanner();

        try {
            MainMenu mainMenu = new MainMenu(scanner);
            mainMenu.start();
        } finally {
            scanner.close();
        }
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║           S H A R E S P A C E            ║");
        System.out.println("  ║    Campus Resource Sharing System v1.0   ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
    }
}
