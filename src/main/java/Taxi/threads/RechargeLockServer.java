package taxi.threads;

public class RechargeLockServer {

    Object rechargeLock;

    public RechargeLockServer() { rechargeLock = new Object(); }

    public void block() {
        synchronized (rechargeLock) {
            try {
                rechargeLock.wait();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }

    public void wakeUp() {
        synchronized (rechargeLock) {
            rechargeLock.notifyAll();
        }
    }
}
