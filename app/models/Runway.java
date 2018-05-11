package models;

import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;

/**
 * Runway entity managed by Ebean
 */
@Entity
@Table(name="runway")
public class Runway extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    public Long id;

    @Constraints.Required
    @Column(name = "airport_id")
    public Long airportId;

    @Column(name = "surface")
    public String surface;
}
