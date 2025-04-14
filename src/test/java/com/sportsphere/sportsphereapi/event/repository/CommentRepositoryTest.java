package com.sportsphere.sportsphereapi.event.repository;

import com.sportsphere.sportsphereapi.event.entity.Comment;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.Location;
import com.sportsphere.sportsphereapi.user.Role;
import com.sportsphere.sportsphereapi.user.User;
import com.sportsphere.sportsphereapi.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Event testEvent;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstname("John")
                .lastname("Doe")
                .password("encrypted-pass")
                .email("john@gmail.com")
                .enabled(true)
                .role(Role.USER)
                .build();
        testUser = entityManager.persist(testUser);

        testLocation = Location.builder()
                .name("Test Park")
                .latitude(new BigDecimal("40.7128"))
                .longitude(new BigDecimal("-74.0060"))
                .city("New York")
                .country("USA")
                .formattedAddress("123 Test St, New York, NY")
                .build();
        testLocation = entityManager.persist(testLocation);

        testEvent = Event.builder()
                .createdBy(testUser)
                .title("Test Event")
                .description("Test Description")
                .startsAt(LocalDateTime.now().plusDays(1))
                .sportCategory("Soccer")
                .sportName("Football")
                .teamNumber(2)
                .playerNumber(11)
                .location(testLocation)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testEvent = entityManager.persist(testEvent);

        entityManager.flush();
    }

    @Test
    void testSaveComment() {
        Comment comment = Comment.builder()
                .event(testEvent)
                .user(testUser)
                .content("This is a test comment")
                .build();

        Comment savedComment = commentRepository.save(comment);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("This is a test comment");
        assertThat(savedComment.getEvent().getId()).isEqualTo(testEvent.getId());
        assertThat(savedComment.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedComment.getCreatedAt()).isNotNull();
        assertThat(savedComment.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindByEventIdOrderByCreatedAtDesc() {
        Comment comment1 = Comment.builder()
                .event(testEvent)
                .user(testUser)
                .content("First comment")
                .build();
        Comment comment2 = Comment.builder()
                .event(testEvent)
                .user(testUser)
                .content("Second comment")
                .build();

        entityManager.persist(comment1);
        try{
            Thread.sleep(50);
        }catch (InterruptedException e){}
        entityManager.persist(comment2);

        Event otherEvent = Event.builder()
                .createdBy(testUser)
                .title("Other Event")
                .description("Other Description")
                .startsAt(LocalDateTime.now().plusDays(2))
                .sportCategory("Basketball")
                .sportName("Basketball")
                .teamNumber(2)
                .playerNumber(5)
                .location(testLocation)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(otherEvent);

        Comment otherComment = Comment.builder()
                .event(otherEvent)
                .user(testUser)
                .content("Other event comment")
                .build();
        entityManager.persist(otherComment);

        entityManager.flush();

        List<Comment> comments = commentRepository.findByEventIdOrderByCreatedAtDesc(testEvent.getId());

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getContent()).isEqualTo("Second comment");
        assertThat(comments.get(1).getContent()).isEqualTo("First comment");
        assertThat(comments.get(0).getCreatedAt()).isAfter(comments.get(1).getCreatedAt());
    }

    @Test
    void testFindByEventIdOrderByCreatedAtDesc_NoComments() {
        List<Comment> comments = commentRepository.findByEventIdOrderByCreatedAtDesc(testEvent.getId());

        assertThat(comments).isEmpty();
    }

    @Test
    void testDeleteComment() {
        Comment comment = Comment.builder()
                .event(testEvent)
                .user(testUser)
                .content("Comment to delete")
                .build();
        Comment savedComment = entityManager.persist(comment);
        entityManager.flush();

        commentRepository.delete(savedComment);
        entityManager.flush();

        Comment deletedComment = entityManager.find(Comment.class, savedComment.getId());
        assertThat(deletedComment).isNull();
    }
}
