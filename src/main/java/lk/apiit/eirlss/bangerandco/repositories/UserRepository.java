package lk.apiit.eirlss.bangerandco.repositories;

import lk.apiit.eirlss.bangerandco.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findUserByEmail(String email);
}
