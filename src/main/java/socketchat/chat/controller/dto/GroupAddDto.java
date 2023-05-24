package socketchat.chat.controller.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupAddDto {
    private String groupName;
    private String groupImgUrl;
    private String userId;
}
