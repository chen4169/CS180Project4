# CS180 - Project 4 Report

## Part One: The Project and its Functionality



## Part Two: Individual Section

### Owen Willis
My main contribution to project 4 was the Market class.  This is where the main method that ran the market interface was.  Users are prompted to enter a Username or create an account.  From there the main method splits into different interfaces depending on whether the user is a buyer or a seller.  Buyers can view the products in the market and buy or add items to their cart.  Sellers can view their products, stores, and sales.   All information that is input or output within the main method is either read from or saved to the database through CSV (or table) files.  All quires to the database were done through the main method, although these were mainly contributions made by my teammates.  My responsibility was integrating all the classes created by my teammates into a functioning program that responds based on user input via the terminal.  Additionally, I contributed to one of the subclasses, the Customer class.  The customer class was used to instantiate users within the main method and associate an ID, name, and password with the user.  This information was in turn used to track each users’ cart and purchase history within CSV files.    Outside of the coding aspect, I also helped plan meetings and break up the tasks that needed completion for project 4.
The only thing I would change if we were to start over again is time management.  Everyone on the team had a lot going on, so it wasn’t anyone’s fault, but starting the project a little earlier and working at a steady pace could have led to a better outcome for the final project.  While the project still represents high effort from every team member, a more concrete schedule or timeline could have reduced stress for team members and made the project seem a bit more manageable.  In terms of coding, I think a little more structure could have been used in the subclasses.  An abstract class or interface for a general “user” could have been implemented to keep the Customer and Seller classes a bit cleaner.




### Jaime Rueda
For project four I took care of all the aspects that were necessary in order to create, connect and integrate a database into the project. The first step was to create the database that would be utilized. To create the database I used Microsoft access due to the integration through Ucanaccess that can be imported into IntelliJ. Once the file was created I created a table for each of the major components of the database which could be constituted as a table, this will allow us to determine what would be the characteristics of each of the classes. Once that is done I created the relationships between the tables in order to connect them and the main reason as to why we are using the database since we can filter the tables through the use of queries. Once the database was set up I integrated the database into Java through the Database class. This class when constructed will create a connection through Ucanaccess and allow java and the database to talk to one another. Furthermore, I was in charge of all of the SQL queries that are used within the code in order to facilitate all of the reading and searching within the information needed.
If I were to start over againI probably would have avoided the integration of the Database since even though the utilization for it is of great use and convenience the integration has been a constant uphill battle with the system. Not only that but most of our errors seem to occur with the fact that the file for some reason is not letting other people get access even though there should not be any restrictions of use. At this point it probably would have been easier to deal with the limitations of the CSV files instead of all the problems that have been created with the integration of the database. For project 5 I believe we have two different options: either we resolve all of our issues with the database and continue down that path or probably restart integrating only the CSV files.


### Libin Chen
//individual writing

### Thomas Eggers
//individual writing


## Part Three: Project 5 Collaboration Strategy





