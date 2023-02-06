package edu.unicauca.apliweb.persistence.entities;

import edu.unicauca.apliweb.persistence.entities.Customer;
import edu.unicauca.apliweb.persistence.entities.Invoiceline;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.10.v20211216-rNA", date="2023-02-06T13:41:50")
@StaticMetamodel(Invoice.class)
public class Invoice_ { 

    public static volatile SingularAttribute<Invoice, BigDecimal> total;
    public static volatile SingularAttribute<Invoice, String> billingPostalCode;
    public static volatile SingularAttribute<Invoice, Customer> customerId;
    public static volatile SingularAttribute<Invoice, String> billingCountry;
    public static volatile SingularAttribute<Invoice, Integer> invoiceId;
    public static volatile SingularAttribute<Invoice, String> billingAddress;
    public static volatile SingularAttribute<Invoice, String> billingState;
    public static volatile SingularAttribute<Invoice, Date> invoiceDate;
    public static volatile SingularAttribute<Invoice, String> billingCity;
    public static volatile ListAttribute<Invoice, Invoiceline> invoicelineList;

}