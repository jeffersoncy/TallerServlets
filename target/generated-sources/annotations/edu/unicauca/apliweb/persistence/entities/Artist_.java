package edu.unicauca.apliweb.persistence.entities;

import edu.unicauca.apliweb.persistence.entities.Album;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.10.v20211216-rNA", date="2023-02-01T20:59:05")
@StaticMetamodel(Artist.class)
public class Artist_ { 

    public static volatile ListAttribute<Artist, Album> albumList;
    public static volatile SingularAttribute<Artist, String> name;
    public static volatile SingularAttribute<Artist, Integer> artistId;

}