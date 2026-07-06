package in.mjunth.resumebuilderjava.repository;

import in.mjunth.resumebuilderjava.document.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<Resume,String> {
    List<Resume> findByUserIdOrderByUpdatedAt(String userId);
    Optional<Resume> findByUserIdAndId(String userId,String id);
    void deleteByUserIdAndId(String userId,String id);
}
