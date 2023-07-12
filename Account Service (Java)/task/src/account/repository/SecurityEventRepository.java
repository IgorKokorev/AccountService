package account.repository;

import account.model.SecurityEvent;
import org.springframework.data.repository.CrudRepository;

public interface SecurityEventRepository extends CrudRepository<SecurityEvent, Long> {

}
