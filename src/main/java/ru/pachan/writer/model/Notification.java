package ru.pachan.writer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "notifications")
public class Notification {

    @Column(nullable = false, unique = true)
    private long personId;

    @Column(nullable = false)
    private long count;

    @Id
    @SequenceGenerator(
            name = "notifications_seq",
            sequenceName = "notifications_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notifications_seq")
    @Column(name = "notification_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

}
