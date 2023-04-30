# CS180Project5
1.
Project 5 of CS 180 class
Please take into account that the filepath needed for the database must be the complete filepath of where the database is downloaded from. The path should be separated by "//". For example (C://Users//path1//path2//paht3//Database1.accdb) and (C://Users//Q1892//IdeaProjects//MyProjects//src//Project5//Database1.accdb)

To install the environment to run the database code, one must download the libs file, then create a new Directory in the project file, copy the files in the libs and paste them into the libs. Next, click "File" on the corner, click "Project Structure" go to "Libraries", clikc "+" to add New Project Library, select "Java", find the libs file just downloaded, click "Ok", that is. Now you can run the databese codes.

2.
Tommy -Submitted Report on Brightspace

Libin Chen - Submitted Vocareum workspace

3.
Database – A class with methods to search information from the database, add product to the database, add market, add seller, add customer, delete account, update information of the database. It is an important class with many useful tools for the program to interact with the database. Methods that would change the value in the database file is synchronized to avoid race condition. And only the Server will use this class for project 5. 

Server - A class that set up the network to handle request from multiple clients, it will create a Database object and use it accordingly. All the requests will be leading with a command index to indecate what the client want the Server to do. It will create a new thread if a new client try to connect to it.

Marketplace – This class is the entry of the program, it has sign in and log in functions for the user to select and it will check if the user is a seller or customer then run the corresponding client class by passing the socket, in, and out objects, and the user informaton.
