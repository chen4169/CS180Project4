# CS180 - Project 4 Report

## Part One: The Project and its Functionality
For project 4 our team decided to go through with option three, which takes on the task of implementing the official marketplace of the application. The
theme of the marketplace is that it will allow sellers to list their products and customers to purchase them. 

(my thoughts are we explain the separate classes first, and then tie them in with how the main method goes about connecting everything)

How the database is integrated:
First we create a connection to the database through the use of Ucanaccess by giving the url of the database. For details on Ucanaccess https://ucanaccess.sourceforge.net/site.html 
From there once the connection was made what needed to be done was to ask the connection to do certain actions in this case we only made SQL queries.
Each query is set by a set of Keywords such as Insert Into Delete and Select.
Once the Query has run it will return the results of such query in the specified order.


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
For our collaboration strategy in Project 5, we've decided to follow the same approach we used in Project 4. This allows us to build upon the collaboration experience we've gained and work together more effectively and efficiently, so we don’t need to make a new plan for our collaboration again.
Firstly, we'll break down the complex program into smaller, simpler cases and assign them to each team member, just like in project 4. We'll then come together as a group to tackle the more difficult cases and work through any bugs by having deep discussions. We'll continue to use the communication channels that worked well for us in Project 4, including email, phone, video conferencing, and collaboration tools. We want to ensure that everyone is comfortable with the tools we use and has easy access to them.
Finally, we'll create a schedule for Project 5 and share it with everyone through our GitHub repository like what we have done for project 4. This will ensure that everyone is aware of the project timeline and can plan their work accordingly. By following this strategy, we believe we can work together more efficiently and effectively, and ultimately achieve a successful outcome for our next project.

For the tasks list, we believe that the most important and challenging implementations are the exchange of information between server and client, the concurrency of threads to avoid race condition, and the GUI windows design. Thus, we might follow the list below for our project 5:
“Develop Server and Client class, along with network connection” – Completed by   04/17/2023 – Jaime Rueda
“Develop concurrency of threads to avoid race condition” – Completed by 04/17/2023 – Owen Willis
“Develop fully-featured GUI” – Completed by 04/17/2023 – Tommy Eggers
“Develop fully-featured GUI windows design” – Completed by 04/17/2023 – Libin Chen
However, we might modify the responsibilities for each of our members when we are writing our program, or we might come together to work on the same part when we are facing challenging issues.

In the event of conflicts, we would try to communicate with our team first to avoid any misunderstandings. We should focus on the problem, not the person, in any event of conflict. When conflicts arise, by focusing on the issue, the team can work together to find a solution. And if a conflict occurred, we would try to find the root of this issue, once the root cause of the conflict is identified, we could brainstorm possible solutions. This can involve considering different perspectives and finding common ground. If the conflict is too difficult to resolve by our team members, we would seek help from our professor and TAs of our class for outside intervenes.






