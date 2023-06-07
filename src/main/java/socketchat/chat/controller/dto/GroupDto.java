package socketchat.chat.controller.dto;

import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupDto {
    private int groupId;
    private String groupName;
    private String groupImgUrl;
    private int people;
}

