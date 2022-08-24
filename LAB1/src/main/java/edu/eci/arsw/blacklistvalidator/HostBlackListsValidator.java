/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N){

        LinkedList<HostBlackListsThread> threads = new LinkedList<HostBlackListsThread>();
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        int ocurrencesCount=0;
        int checkedListsCount=0;
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();

        int fraction = 0;
        if(N % 2 != 0){
            fraction = (skds.getRegisteredServersCount()- (skds.getRegisteredServersCount()%N)) / N;
        }
        else{
            fraction = (skds.getRegisteredServersCount() / N);
        }
        Object lock = new Object();
        for(int i=0; i<N; i++){
            int from = fraction*i;
            int to = fraction*(i+1);
            threads.add(new HostBlackListsThread(ipaddress,ocurrencesCount,checkedListsCount,from,to,skds, lock));
        }

        threads.forEach((thread)-> thread.start());

        for (HostBlackListsThread thread: threads) {
            try {
                thread.join();
                checkedListsCount += thread.getCheckedListCount();
                ocurrencesCount += thread.getOcurrencesCount();
                thread.getServers().forEach(server-> blackListOcurrences.add(server));
                if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
                    break;
                }
            }catch (Exception e){
                LOG.info(e.getMessage());
            }
        }
        
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
            LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});

            threads.forEach(thread-> {
                try {
                    thread.stopThread();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        

        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
