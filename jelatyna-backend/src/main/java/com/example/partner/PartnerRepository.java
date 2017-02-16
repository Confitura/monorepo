package com.example.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "partners")
public interface PartnerRepository extends CrudRepository<Partner, Long> {
}
