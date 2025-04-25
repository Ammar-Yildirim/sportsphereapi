package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.repository.EventRepository;
import com.sportsphere.sportsphereapi.exception.CustomException;
import com.sportsphere.sportsphereapi.user.User;
import com.sportsphere.sportsphereapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public List<User> getUsers(){
        try{
            return userRepository.findAllNonAdmins();
        }catch (Exception e){
            throw new CustomException("Database Error", "Error while fetching Users", HttpStatus.NO_CONTENT);
        }
    }

    public List<Event> getEvents(){
        try{
            return eventRepository.findAll();
        }catch (Exception e){
            throw new CustomException("Database Error", "Error while fetching Users", HttpStatus.NO_CONTENT);
        }
    }

    public void deleteUser(UUID userId) {
        try{
            userRepository.deleteById(userId);
        }catch (Exception e){
            throw new CustomException("Database Error", "Error while fetching Users", HttpStatus.NO_CONTENT);
        }
    }

    public void deleteEvent(UUID eventId){
        try{
            eventRepository.deleteById(eventId);
        }catch (Exception e){
            throw new CustomException("Database Error", "Error while fetching Users", HttpStatus.NO_CONTENT);
        }
    }
}
