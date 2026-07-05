package in.mjunth.resumebuilderjava.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Email is  Required")
    @Email(message = "Enter Valid Email")
    private String email;

    @NotBlank(message = "Name is Reruired")
    @Size(min = 2, max = 15,message = "Name should be between 2 to 15 characters")
    private String name;

    @NotBlank(message = "Password is Required")
    @Size(min = 6,max = 15, message = "Password should be between 6 to 15 characters")
    private String password;
    private String profileImageUrl;
}
