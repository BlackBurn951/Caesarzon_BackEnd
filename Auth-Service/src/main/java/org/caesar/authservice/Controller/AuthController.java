package org.caesar.authservice.Controller;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.caesar.authservice.Config.JwtUtil;
import org.caesar.authservice.Entity.Tokens;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth-api")
@CrossOrigin(origins = "http://localhost:4200/")
@RequiredArgsConstructor
public class AuthController {


    @PostMapping(value = "/login")
    public String login(@RequestBody Tokens tokens) throws Exception {
        JwtUtil.parseToken(tokens.getAccess());
        return "Tokens ricevuti";
    }

}
