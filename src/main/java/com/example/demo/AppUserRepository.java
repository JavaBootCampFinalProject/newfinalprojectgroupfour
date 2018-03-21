package com.example.demo;

import org.springframework.data.repository.CrudRepository;

public interface AppUserRepository  extends CrudRepository<AppUser,Long> {

    AppUser findAppUserByUsername(String username);
    int countAllByApplied(Programs prog);
    int countAllByApproved(Programs prog);
}
