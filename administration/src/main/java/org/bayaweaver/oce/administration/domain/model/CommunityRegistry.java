package org.bayaweaver.oce.administration.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CommunityRegistry {
    private static final CommunityRegistry instance = new CommunityRegistry();

    private final Collection<Community> communities;

    private CommunityRegistry() {
        this.communities = new ArrayList<>();
    }

    public static CommunityRegistry instance() {
        return instance;
    }

    public Community community(CommunityId id) {
        for (Community community : this.communities) {
            if (community.id.equals(id)) {
                return community;
            }
        }
        throw new IllegalArgumentException("Community '" + id + "' is absent.");
    }

    public void registerCommunity(CommunityId id) {
        this.communities.add(new Community(id));
    }

    public void dissolveCommunity(CommunityId id) {
        Community community = community(id);
        this.communities.remove(community);
        for (BallotingCalendar.Election election : this.elections) {
            if (election.community().equals(community)) {
                election.cancel();
            }
        }
        this.electionRequesters.remove(community);
    }
    
    public class Community {
        private final CommunityId id;
        private final Set<MemberId> members;

        private Community(CommunityId id) {
            this.id = id;
            this.members = new HashSet<>();
        }

        private CommunityId id() {
            return id;
        }

        private Iterable<MemberId> members() {
            return members;
        }

        public void registerMember(MemberId member) {
            for (Community community : communities) {
                if (community.equals(this)) {
                    continue;
                }
                for (MemberId alreadyRegistered : community.members) {
                    if (alreadyRegistered.equals(member)) {
                        throw new IllegalArgumentException("A member can be registered in only one community.");
                    }
                }
            }
            this.members.add(member);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Community that = (Community) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}
