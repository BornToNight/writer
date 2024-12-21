package ru.pachan.writer.service.writerDtoConsumer;

import ru.pachan.writer.dto.WriterDto;

import java.util.List;

public interface WriterDtoConsumer {

    void accept(List<WriterDto> writerDtoList);

}
