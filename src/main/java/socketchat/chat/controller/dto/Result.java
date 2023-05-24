package socketchat.chat.controller.dto;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class Result<T> {
    private final T result;
}
