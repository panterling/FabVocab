package uk.co.cdevelop.fabvocab.SQL.Models;

/**
 * Created by Chris on 12/03/2017.
 */

public class DefinitionEntry {
    private int id;
    private String definition;

    public DefinitionEntry(int id, String definition) {
        this.definition = definition;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDefinition() {
        return definition;
    }

    @Override
    public String toString() {
        return this.definition;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return  false;
        } else if(!(obj instanceof DefinitionEntry)) {
            return false;
        } else {
            DefinitionEntry other = (DefinitionEntry) obj;
            return this.definition.equals(other.getDefinition());
        }
    }

}
