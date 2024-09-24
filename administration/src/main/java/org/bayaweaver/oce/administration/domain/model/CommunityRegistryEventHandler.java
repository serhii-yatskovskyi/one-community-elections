package org.bayaweaver.oce.administration.domain.model;

import java.util.Observable;
import java.util.Observer;

public class CommunityRegistryEventHandler implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof CommunityDissolvedEvent event) {
            for (BallotingCalendar.Election election : BallotingCalendar.instance().electionsOf(event.community())) {
                election.cancel();
            }
        }
    }
}
