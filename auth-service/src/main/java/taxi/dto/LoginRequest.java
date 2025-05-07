package taxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Авторизация")
public class LoginRequest {
    @Schema(description = "Имя пользователя")
    private String username;

    @Schema(description = "Пароль")
    private String password;
}

