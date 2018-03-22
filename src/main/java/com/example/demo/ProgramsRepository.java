package com.example.demo;

import org.springframework.data.repository.CrudRepository;

public interface ProgramsRepository extends CrudRepository<Programs,Long> {
    Programs findByCourseName(String course);
    Programs findByUserApplied(AppUser appUser);
    Programs findByUserApproved(AppUser appUser);

}
