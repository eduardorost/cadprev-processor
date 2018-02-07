package com.cadprev.repositories;

import com.cadprev.entities.ErroEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErroRepository extends CrudRepository<ErroEntity, Long> {
}
