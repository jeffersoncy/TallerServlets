package edu.unicauca.apliweb.persistence.entities;

import edu.unicauca.apliweb.persistence.entities.Album;
import edu.unicauca.apliweb.persistence.entities.Genre;
import edu.unicauca.apliweb.persistence.entities.Invoiceline;
import edu.unicauca.apliweb.persistence.entities.Mediatype;
import edu.unicauca.apliweb.persistence.entities.Playlist;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.10.v20211216-rNA", date="2023-02-01T20:59:05")
@StaticMetamodel(Track.class)
public class Track_ { 

    public static volatile SingularAttribute<Track, BigDecimal> unitPrice;
    public static volatile SingularAttribute<Track, Genre> genreId;
    public static volatile SingularAttribute<Track, Integer> milliseconds;
    public static volatile ListAttribute<Track, Playlist> playlistList;
    public static volatile SingularAttribute<Track, Integer> trackId;
    public static volatile SingularAttribute<Track, String> composer;
    public static volatile SingularAttribute<Track, Integer> bytes;
    public static volatile SingularAttribute<Track, String> name;
    public static volatile SingularAttribute<Track, Mediatype> mediaTypeId;
    public static volatile SingularAttribute<Track, Album> albumId;
    public static volatile ListAttribute<Track, Invoiceline> invoicelineList;

}