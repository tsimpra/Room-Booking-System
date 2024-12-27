package com.acme.rbs.repository;

import com.acme.rbs.domain.AcmeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcmeUserRepository extends JpaRepository<AcmeUser, Long> {
}