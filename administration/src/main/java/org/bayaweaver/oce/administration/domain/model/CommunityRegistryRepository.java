package org.bayaweaver.oce.administration.domain.model;

public class CommunityRegistryRepository {
    private final CommunityRegistry singleEntity;

    public CommunityRegistryRepository() {
        singleEntity = new CommunityRegistry();
    }

    public CommunityRegistry get() {
        return singleEntity;
    }
}
