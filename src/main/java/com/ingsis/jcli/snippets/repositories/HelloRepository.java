package com.ingsis.jcli.snippets.repositories;

import com.ingsis.jcli.snippets.models.Hello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelloRepository extends JpaRepository<Hello, Long> {}
