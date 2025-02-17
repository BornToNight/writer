package ru.pachan.writer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pachan.writer.model.Notification;

import java.util.List;
import java.util.Set;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(
            """
            FROM Notification
            WHERE
                (personId IN (:personIds))
            """
    )
    List<Notification> findAll(
            @Param("personIds") Set<Long> personIds
    );

}
