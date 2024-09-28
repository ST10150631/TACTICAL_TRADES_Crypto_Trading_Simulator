# OPSC7312_POE_TACTICAL_TRADES READMEFILE

---

### Table of Contents
You're sections headers will be used to reference location of destination.

- [Description](#description)
- [How To Use](#how-to-use)
- [Technologies](#technologies)
- [API'S](#API'S)
- [License](#license)
- [Author Info](#author-info)

---

## Description
Tactical Trades is a cryptocurrency trading application that allows users to practice trading some of the largest cryptocurrencies in the world without spending money. This app makes use of real-time trading data using the same APIs as many big trading platforms to provide users with a completely accurate trading simulation. Tactical trades have many features to improve user experience such as colourblind graph filters, user-friendly interfaces, news feeds and advanced analytical data to ensure that the user has access to all the data possible to make informed trades. The app will also have difficulty selections allowing users to select their starting amount and will have goals the user can achieve.

#### Technologies
- Android Studio 
- Kotlin
- Firebase

### API'S
- CoinAPI
- NewsAPI

[Back To The Top](#read-me-template)

---

## How To Use

### How to register
- To register the user must enter in their Username,Name,Email,Password, and lastly to confirm their password
- Requirements for the password is that it must have a capital letter, a number, and a special character.
- Once the user has entered in all of the required data the user must click the register button and this will take the user to the Home screen.
- If the user already has an account they are able to click the "Click here to login" button and this will take the user to the login screen.
  
![image](https://github.com/user-attachments/assets/5148c9ba-287c-4f02-957c-be190d3fb41d)


### How to Login
- When the user has launched the program he will be met with the login page
- if the user has an account already he must enter in his username and password
- if the username and password do not match the user will be displayed with a message
- that says the password does not exist
- once the user has logged in they will be displayed with the home page.

  ### Features on the home page
  - Once the user has logged in the will be displayed with the home page.
  - The user will be displayed with a calender that will show them the current date.
  - The user will the be shown a graph that if the click the show all hours it will show the user all of the modules that are connected to the semesters and the hours completed for each
 
  
### How to add a semester
 - The user must click the button that says add semester on the left
 - Once the user has clicked the add semesters button they must enter the Semester number and the number of weeks and then select the date at which the semester starts
 - Once the user has selected all of the above requirements they must then select the add semester button
 - this will add the semester to the database

   ### How to add a Module
  -Once the user has added a semester and wish to add modules
  - They must select the add modules button on the left
  - once the user has selected the add users button
  - they will prompted with 5 text boxes
  - the user must select the semester number they want to add the module to by clicken on the combo box
  - once they have selected the semester they want to add the module to they must then enter the module code,number of credits,module name, and class hours per week
  - once they have entered the details they must then select the add modules button
  - this will then add the module to the semester 
### How to Display Modules
-Once the user has added the semester and modules they want the user is able to display all of the modules that are connected to that semester
- by clicking on the Display Modules button on the left
- Once the user has selected that button they must select the semester they wish to display
- and then they must click the display module details button
- this will then display all of the modules connected to that semester in the table below 
 

### How to Add a record
 - If the user wishes to add the hours he spent on a module the user must click on the add records button
 - The user then select the SEMESTER first and then click select semester button
 - the Module Code combo box will then be populated with the modules that are connected to the semester
 - the user can then enter the hours he spent on the module and then select the date
 - the user must the click the add hours button this will the save the record to the database.

   ### How to Display a record and add hours to records
  - Once the user has added a record they are then able to display the record
  - to display the record the user must click the display records button on the left
  - the user must then select the semester they wish to display and then click the select semester button
  - once the user has selected the select semester button the Module Code combo box will be populated
  - the user must then select the module they wish to display
  - the user must then click the display records button
  - once the display record button has been clicked it will display all of the records connect to that module and semester
  - if the user wishes to add more hours spent to the module
  - they must select the row they wish to edit and then enter the hours they have completed
  - and then the user must click the edit records button
  - this will then update the hours completed and hours left column of the record.


  - 
## License

MIT License

Copyright (c) [2017] [James Q Quick]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
