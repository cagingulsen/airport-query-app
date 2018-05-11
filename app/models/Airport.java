package models;

import io.ebean.Model;

import javax.persistence.*;

/**
 * Airport entity managed by Ebean
 */
@Entity
@Table(name="airport")
public class Airport extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    @Column(name = "name")
    public String name;

    @Column(name = "atype")
    public String atype;

    @Column(name = "country_code")
    public String countryCode;

    @ManyToOne
    public Country country;
}

