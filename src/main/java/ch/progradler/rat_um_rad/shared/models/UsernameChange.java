package ch.progradler.rat_um_rad.shared.models;

import java.util.Objects;

public class UsernameChange {
    private final String oldName;
    private final String newName;

    public UsernameChange(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsernameChange that = (UsernameChange) o;
        return oldName.equals(that.oldName) && newName.equals(that.newName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldName, newName);
    }
}
