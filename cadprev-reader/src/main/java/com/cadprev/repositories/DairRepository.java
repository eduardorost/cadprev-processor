package com.cadprev.repositories;

import com.cadprev.entities.DairEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DairRepository extends CrudRepository<DairEntity, Long> {
}
