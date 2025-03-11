package com.sportsphere.sportsphereapi.event.Entity;

import com.sportsphere.sportsphereapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "sport_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name="starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "sport_category", nullable = false)
    private String sportCategory;

    @Column(name = "sport_name", nullable = false)
    private String sportName;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(name = "team_number", nullable = false)
    private Integer teamNumber;

    @Column(name = "player_number", nullable = false)
    private Integer playerNumber;

    private Double latitude;

    private Double longitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}