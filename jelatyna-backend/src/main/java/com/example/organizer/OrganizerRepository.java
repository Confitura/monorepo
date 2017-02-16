package com.example.organizer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "organizers")
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
}
