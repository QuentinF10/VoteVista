import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NetworkCheck {

    private VoteVistaUI ui; // Reference to your UI class

    public NetworkCheck(VoteVistaUI ui) {
        this.ui = ui;
    }

    public void startNetworkMonitoring() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable periodicTask = () -> {
            try {
                if (!isInternetReachable()) {
                    ui.updateUIForNetworkIssue(); // Call the UI update method
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Schedule the task to run every 10 seconds
        executor.scheduleAtFixedRate(periodicTask, 0, 2, TimeUnit.SECONDS);
    }

    public static boolean isInternetReachable() {
        try {
            InetAddress address = InetAddress.getByName("8.8.8.8");
            return address.isReachable(5000);  // Timeout in milliseconds
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
