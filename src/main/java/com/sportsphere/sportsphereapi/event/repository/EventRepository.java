package com.sportsphere.sportsphereapi.event.repository;

import com.sportsphere.sportsphereapi.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    
}
