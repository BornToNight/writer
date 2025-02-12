package ru.pachan.writer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import ru.pachan.writer.model.Notification;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByPersonId(long personId);

    @Query(
            """
            FROM Notification
            WHERE
                (personId IN (:personIds))
            """
    )
    List<Notification> findAll(
            @RequestParam("personIds") List<Long> personIds
    );

}
