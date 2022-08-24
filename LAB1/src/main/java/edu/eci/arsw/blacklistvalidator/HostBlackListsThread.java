package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.LinkedList;
import java.util.logging.Logger;

public class HostBlackListsThread extends Thread{

    private static final Logger LOG = Logger.getLogger(HostBlackListsThread.class.getName());
    private final String ipAddress;
    private int ocurrencesCount;
    private int checkedListCount;
    private final int from;
    private final int to;
    private final HostBlacklistsDataSourceFacade skds;
    private LinkedList<Integer> servers = new LinkedList<>();
    private final Object lock;

    public HostBlackListsThread(String ipAddress, int ocurrencesCount, int checkedListCount, int from, int to, HostBlacklistsDataSourceFacade skds, Object lock) {
        this.ipAddress = ipAddress;
        this.ocurrencesCount = ocurrencesCount;
        this.checkedListCount = checkedListCount;
        this.from = from;
        this.to = to;
        this.skds = skds;
        this.lock = lock;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getOcurrencesCount() {
        return ocurrencesCount;
    }

    public void setOcurrencesCount(int ocurrencesCount) {
        this.ocurrencesCount = ocurrencesCount;
    }

    public int getCheckedListCount() {
        return checkedListCount;
    }

    public void setCheckedListCount(int checkedListCount) {
        this.checkedListCount = checkedListCount;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public HostBlacklistsDataSourceFacade getSkds() {
        return skds;
    }

    public LinkedList<Integer> getServers() {
        return servers;
    }

    public void setServers(LinkedList<Integer> servers) {
        this.servers = servers;
    }

    public void run() {
        for (int i = from; i < to; i++) {
            checkedListCount++;
            if(skds.isInBlackListServer(i, ipAddress)){
                servers.add(i);
                ocurrencesCount++;
            }
        }
    }


    public void stopThread() throws InterruptedException {
        synchronized (lock){
            lock.wait();
        }
    }

}
