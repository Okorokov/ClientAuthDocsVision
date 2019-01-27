package com.example.hpsus.clientauthdocsvision.Model;

import java.util.UUID;

public class Databases {

    private String DatabaseId;
    private String DisplayName;
    private boolean IsDefault;
    private UUID DatabaseIdUuid;

    public Databases() {
    }

    public Databases(String databaseId, String displayName, boolean isDefault) {
        DatabaseId = databaseId;
        DisplayName = displayName;
        IsDefault = isDefault;
    }

    public String getDatabaseId() {
        return DatabaseId;
    }

    public void setDatabaseId(String databaseId) {
        DatabaseId = databaseId;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public boolean isDefault() {
        return IsDefault;
    }

    public void setDefault(boolean aDefault) {
        IsDefault = aDefault;
    }

    public UUID getDatabaseIdUuid() {

        return UUID.fromString(DatabaseId);
    }
}
