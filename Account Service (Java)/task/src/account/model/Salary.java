package account.model;

import account.DTO.PostSalaryRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "salary")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne/*(fetch = FetchType.EAGER)*/
    @JoinColumn(nullable = false, name = "employee_id")
    private User user;

    private String period;

    private int month;

    private int year;

    private Long salary;

    public Salary(User user, String period, Long salary) {
        this.user = user;
        this.period = period;
        this.salary = salary;

        String[] split = period.split("-");
        this.month = Integer.parseInt(split[0]);
        this.year = Integer.parseInt(split[1]);
    }
}
