package com.example.demo;

import org.springframework.data.repository.CrudRepository;

public interface UserCatagoriesRepository  extends CrudRepository<UserCategories,Long> {
    //UserCategories findAllByUserCategoriesByUsername(Long username);

}
