package models;

import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;

/**
 * Country entity managed by Ebean
 */
@Entity
@Table(name="country")
public class Country extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "code")
    public String code;

    @Constraints.Required
    @Column(name = "cname")
    public String name;

    @Transient
    @Column(name = "airport_count")
    public Integer airportCount;

    @Transient
    @Column(name = "surface")
    public String surface;
}

