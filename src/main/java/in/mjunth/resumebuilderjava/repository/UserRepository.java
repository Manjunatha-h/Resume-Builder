package in.mjunth.resumebuilderjava.repository;

import in.mjunth.resumebuilderjava.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String id);

    Optional<User> findByVerificationToken(String token);

}
