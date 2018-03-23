package com.example.demo;

import org.springframework.data.repository.CrudRepository;

public interface ProgramsRepository extends CrudRepository<Programs,Long> {
    Programs findByCourseName(String course);
    Iterable<Programs> findByUserApplied(AppUser appUser);
    Iterable<Programs> findByUserApproved(AppUser appUser);

}
