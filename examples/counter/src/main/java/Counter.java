import io.pcp.parfait.MonitoredCounter;

public class Counter implements Runnable {
    private MonitoredCounter counter;
    private int time = 1000;

    Counter () {
        counter = new MonitoredCounter("example.counter", "A simple Counter that increments once per second");
    }

    public void run () {
        try {
            while (true) {
                counter.inc();
                System.out.println("Counter set to: " + counter.get());
                Thread.sleep(time);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
