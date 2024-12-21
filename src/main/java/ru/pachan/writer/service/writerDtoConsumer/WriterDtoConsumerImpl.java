package ru.pachan.writer.service.writerDtoConsumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pachan.writer.dto.WriterDto;
import ru.pachan.writer.model.Notification;
import ru.pachan.writer.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class WriterDtoConsumerImpl implements WriterDtoConsumer {

    private final NotificationRepository repository;

    @Transactional
    @Override
    public void accept(List<WriterDto> writerDtoList) {
        log.info("start WriterDtoConsumerImpl.accept for {}", writerDtoList);
        List<Notification> notificationList = writerDtoList.stream().collect(
                Collectors.groupingBy(
                        WriterDto::personId,
                        Collectors.summingInt(WriterDto::count))
        ).entrySet().stream().map(writerDtoEntry -> {
                    Optional<Notification> notification = repository.findByPersonId(writerDtoEntry.getKey());
                    if (notification.isPresent()) {
                        notification.get().setCount(notification.get().getCount() + writerDtoEntry.getValue());
                        return notification.get();
                    } else {
                        Notification newNotification = new Notification();
                        newNotification.setPersonId(writerDtoEntry.getKey());
                        newNotification.setCount(writerDtoEntry.getValue());
                        return newNotification;
                    }
                }
        ).toList();
        repository.saveAll(notificationList);
    }

}
