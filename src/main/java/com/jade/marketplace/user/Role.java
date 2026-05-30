package com.jade.marketplace.user;

import com.jade.marketplace.common.constants.RoleConstants;

/**
 * Represents the roles a user can have in the marketplace.
 *
 * BUYER  -> Can browse products and place orders
 * SELLER -> Can create product listings and manage inventory
 * ADMIN  -> Can manage platform-level data
 */
public enum Role {

    BUYER(RoleConstants.ROLE_BUYER),
    SELLER(RoleConstants.ROLE_SELLER),
    ADMIN(RoleConstants.ROLE_ADMIN);

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    /**
     * Returns the Spring Security authority name.
     *
     * Spring Security expects roles to usually look like:
     * ROLE_BUYER, ROLE_SELLER, ROLE_ADMIN
     * 
     * Role role = Role.ADMIN
     * is read "ROLE_ADMIN" in Spring Security
     */
    public String getAuthority() {
        return authority;
    }
}