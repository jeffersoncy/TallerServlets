package edu.unicauca.apliweb.persistence.entities;

import edu.unicauca.apliweb.persistence.entities.Invoice;
import edu.unicauca.apliweb.persistence.entities.Track;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.10.v20211216-rNA", date="2023-02-06T13:41:50")
@StaticMetamodel(Invoiceline.class)
public class Invoiceline_ { 

    public static volatile SingularAttribute<Invoiceline, BigDecimal> unitPrice;
    public static volatile SingularAttribute<Invoiceline, Integer> quantity;
    public static volatile SingularAttribute<Invoiceline, Track> trackId;
    public static volatile SingularAttribute<Invoiceline, Integer> invoiceLineId;
    public static volatile SingularAttribute<Invoiceline, Invoice> invoiceId;

}