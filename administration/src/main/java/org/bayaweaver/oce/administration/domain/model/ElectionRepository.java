package org.bayaweaver.oce.administration.domain.model;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ElectionRepository extends AbstractCollection<Election> {
    private final Collection<Election> elections;

    public ElectionRepository() {
        this.elections = new ArrayList<>();
    }

    @Override
    public Iterator<Election> iterator() {
        return elections.iterator();
    }

    @Override
    public int size() {
        return elections.size();
    }

    @Override
    public boolean add(Election e) {
        if (this.elections.contains(e)) {
            throw new IllegalArgumentException("Election '" + e + "' already exists.");
        }
        return this.elections.add(e);
    }
}
