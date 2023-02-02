package edu.unicauca.apliweb.persistence.entities;

import edu.unicauca.apliweb.persistence.entities.Track;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.10.v20211216-rNA", date="2023-02-01T20:59:05")
@StaticMetamodel(Genre.class)
public class Genre_ { 

    public static volatile SingularAttribute<Genre, Integer> genreId;
    public static volatile ListAttribute<Genre, Track> trackList;
    public static volatile SingularAttribute<Genre, String> name;

}