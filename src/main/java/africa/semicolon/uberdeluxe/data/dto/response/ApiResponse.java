package africa.semicolon.uberdeluxe.data.dto.response;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ApiResponse {
    private int status;
    private String message;
}
