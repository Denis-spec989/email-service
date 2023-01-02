package github.denisspec989.emailservice.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailDTO {
    List<String> toAddress;
    String subject;
    String message;
    List<AttachementDTO> attachment;
}
