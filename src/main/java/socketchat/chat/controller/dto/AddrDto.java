package socketchat.chat.controller.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddrDto {
    String addrInet4;
    int port;
}
