package com.animerec.chat.repositories;

import com.animerec.chat.models.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Integer> {

    Optional<DataSource> findByName(String name);
}
