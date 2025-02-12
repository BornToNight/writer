package ru.pachan.writer.service.writerDtoConsumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pachan.writer.dto.WriterDto;
import ru.pachan.writer.model.Notification;
import ru.pachan.writer.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        // Группируем данные по personId
        Map<Long, Integer> groupedData = writerDtoList.stream()
                .collect(Collectors.groupingBy(WriterDto::personId, Collectors.summingInt(WriterDto::count)));

        // Получаем все существующие уведомления для personId из списка
        List<Notification> existingNotifications = repository.findAll(new ArrayList<>(groupedData.keySet()));
        Map<Long, Notification> notificationMap = existingNotifications.stream()
                .collect(Collectors.toMap(Notification::getPersonId, notification -> notification));

        // Создаем или обновляем уведомления
        List<Notification> notificationList = groupedData.entrySet().stream()
                .map(entry -> {
                    Long personId = entry.getKey();
                    int count = entry.getValue();

                    Notification notification = notificationMap.get(personId);
                    if (notification != null) {
                        // Если уведомление существует, обновляем его
                        notification.setCount(notification.getCount() + count);
                    } else {
                        // Если уведомления нет, создаем новое
                        notification = new Notification();
                        notification.setPersonId(personId);
                        notification.setCount(count);
                    }
                    return notification;
                })
                .collect(Collectors.toList());

        repository.saveAll(notificationList);
    }

}
