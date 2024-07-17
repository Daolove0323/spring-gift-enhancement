package gift.controller.auth;

import java.util.UUID;

public record LoginResponse(UUID id, String email, String nickname, String grade) {

    public boolean isAdmin() {
        return grade != null && grade.equals("admin");
    }
}