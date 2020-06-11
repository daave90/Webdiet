package pl.dave.project.webdietserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "product")
public class Product extends AbstractEntity {
    private String name;
    private double kcal;

    @ManyToOne
    @JoinColumn(name = "USERGUID")
    private User user;

    @Override
    public String toString() {
        return name + " " + kcal + " kcal " + user;
    }
}
