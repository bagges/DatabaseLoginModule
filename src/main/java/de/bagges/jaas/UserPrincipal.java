package de.bagges.jaas;

import java.io.Serializable;
import java.security.Principal;

/**
 * Created by markus on 10.12.14.
 */
public class UserPrincipal implements Principal, Serializable {

    private String username;

    public UserPrincipal(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPrincipal that = (UserPrincipal) o;

        if (!username.equals(that.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "Principal: " + getName();
    }
}
