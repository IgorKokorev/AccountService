package account.repository;

import account.model.Salary;
import account.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends CrudRepository<Salary, Long> {
    public List<Salary> findByUserOrderByYearDescMonthDesc(User user);
    public Optional<Salary> findByUserAndPeriod(User user, String period);
}
