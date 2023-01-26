package edu.unicauca.apliweb.persistence.entities;

import edu.unicauca.apliweb.persistence.entities.Artist;
import edu.unicauca.apliweb.persistence.entities.Track;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.10.v20211216-rNA", date="2023-01-25T15:55:50")
@StaticMetamodel(Album.class)
public class Album_ { 

    public static volatile ListAttribute<Album, Track> trackList;
    public static volatile SingularAttribute<Album, Integer> albumId;
    public static volatile SingularAttribute<Album, Artist> artistId;
    public static volatile SingularAttribute<Album, String> title;

}