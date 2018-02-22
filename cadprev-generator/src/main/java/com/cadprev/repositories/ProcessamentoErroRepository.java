package com.cadprev.repositories;

import com.cadprev.entities.ProcessamentoErroEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessamentoErroRepository extends CrudRepository<ProcessamentoErroEntity, Long> {
}
