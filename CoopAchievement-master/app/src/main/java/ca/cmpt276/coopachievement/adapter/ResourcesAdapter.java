package ca.cmpt276.coopachievement.adapter;

import androidx.annotation.NonNull;

public class ResourcesAdapter {
        
    private final String rName;
    private final String rLink;

    public ResourcesAdapter(String name, String link) {
        this.rName = name;
        this.rLink = link;
    }

    public String getLink() {
        return rLink;
    }

    @NonNull
    @Override
    public String toString() {
        return this.rName;
    }
}
