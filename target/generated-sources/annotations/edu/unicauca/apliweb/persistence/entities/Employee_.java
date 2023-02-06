package edu.unicauca.apliweb.persistence.entities;

import edu.unicauca.apliweb.persistence.entities.Customer;
import edu.unicauca.apliweb.persistence.entities.Employee;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.10.v20211216-rNA", date="2023-02-06T10:42:40")
@StaticMetamodel(Employee.class)
public class Employee_ { 

    public static volatile SingularAttribute<Employee, String> lastName;
    public static volatile SingularAttribute<Employee, String> country;
    public static volatile SingularAttribute<Employee, Date> hireDate;
    public static volatile SingularAttribute<Employee, String> address;
    public static volatile SingularAttribute<Employee, String> city;
    public static volatile SingularAttribute<Employee, String> postalCode;
    public static volatile SingularAttribute<Employee, Integer> employeeId;
    public static volatile SingularAttribute<Employee, Employee> reportsTo;
    public static volatile SingularAttribute<Employee, String> title;
    public static volatile SingularAttribute<Employee, Date> birthDate;
    public static volatile SingularAttribute<Employee, String> firstName;
    public static volatile ListAttribute<Employee, Employee> employeeList;
    public static volatile SingularAttribute<Employee, String> phone;
    public static volatile ListAttribute<Employee, Customer> customerList;
    public static volatile SingularAttribute<Employee, String> state;
    public static volatile SingularAttribute<Employee, String> fax;
    public static volatile SingularAttribute<Employee, String> email;

}