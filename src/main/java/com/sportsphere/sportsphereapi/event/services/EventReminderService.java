package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.EventReminderLog;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.repository.EventParticipationRepository;
import com.sportsphere.sportsphereapi.event.repository.EventReminderLogRepository;
import com.sportsphere.sportsphereapi.event.repository.EventRepository;
import com.sportsphere.sportsphereapi.user.User;
import com.sportsphere.sportsphereapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventReminderService {

    private final EventRepository eventRepository;
    private final EventParticipationRepository participationRepository;
    private final EmailService emailService;
    private final EventReminderLogRepository eventReminderLogRepository;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void sendEventReminders() {
        log.info("Checking for upcoming events to send reminders");

        LocalDateTime startWindow = LocalDateTime.now().plusMinutes(25);
        LocalDateTime endWindow = LocalDateTime.now().plusMinutes(35);

        List<Event> upcomingEvents = eventRepository.findByStartsAtBetweenOrderByStartsAt(startWindow, endWindow);
        log.info("Found {} events starting between {} and {}", upcomingEvents.size(), startWindow, endWindow);

        for (Event event : upcomingEvents) {
            List<EventParticipation> participations = participationRepository.findByEventParticipationIDEventID(event.getId());

            for (EventParticipation participation : participations) {
                User user = participation.getUser();
                if (eventReminderLogRepository.existsByEventIdAndUserId(
                        event.getId(), user.getId())) {
                    continue;
                }

                if (user.getEmail() == null || user.getFirstname() == null || event.getLocation() == null) {
                    log.warn("Skipping reminder for user {} or event {} due to missing data", user.getId(), event.getId());
                    continue;
                }

                EventReminderLog eventReminderLog = new EventReminderLog();
                eventReminderLog.setEvent(event);
                eventReminderLog.setUser(user);
                eventReminderLog.setSentAt(LocalDateTime.now());

                try {
                    sendReminderEmail(user, event);
                    eventReminderLog.setStatus("SENT");
                    log.info("Sent reminder email to {} for event {}", user.getEmail(), event.getId());
                } catch (Exception e) {
                    eventReminderLog.setStatus("FAILED");
                    eventReminderLog.setErrorMessage(e.getMessage());
                    log.error("Failed to send reminder email to {} for event {}: {}", user.getEmail(), event.getId(), e.getMessage());
                }

                eventReminderLogRepository.save(eventReminderLog);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void cleanupOldReminders() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        eventReminderLogRepository.deleteBySentAtBefore(cutoff);
    }

    private void sendReminderEmail(User user, Event event) {
        String formattedTime = event.getStartsAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String subject = "Reminder: Your event '" + event.getTitle() + "' starts in 30 minutes!";

        String body = String.format(
                "Hello %s,\n\n" +
                        "This is a reminder that your event '%s' starts in about 30 minutes at %s.\n\n" +
                        "Location: %s | %s\n" +
                        "Sport: %s (%s)\n\n" +
                        "Don't be late! See you there!\n\n" +
                        "Best regards,\n" +
                        "SportSphere Team",
                user.getFirstname(),
                event.getTitle(),
                formattedTime,
                event.getLocation().getName(),
                event.getLocation().getFormattedAddress(),
                event.getSportName(),
                event.getSportCategory()
        );

        try {
            emailService.sendSimpleEmail(user.getEmail(), subject, body);
            log.info("Sent reminder email to {} for event {}", user.getEmail(), event.getId());
        } catch (Exception e) {
            log.error("Failed to send reminder email to {} for event {}: {}",
                    user.getEmail(), event.getId(), e.getMessage());
        }
    }
}