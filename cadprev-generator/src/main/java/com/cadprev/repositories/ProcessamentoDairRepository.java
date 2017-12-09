package com.cadprev.repositories;

import com.cadprev.entities.ProcessamentoDairEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessamentoDairRepository extends CrudRepository<ProcessamentoDairEntity, Long> {

    @Query(value = "SELECT DISTINCT uf FROM processamento_dair ORDER BY uf", nativeQuery = true)
    List<String> findAllUF();

    @Query(value = "SELECT cidade FROM processamento_dair WHERE uf = ?1", nativeQuery = true)
    List<String> findAllCidadesBYUF(String uf);
}
