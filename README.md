# WorkReport

This application was built for a former employer of mine as a practice project.  My goal was to solve an issue with an app, and build that application out and publish it to the
Google Play Store for use. 

The issue:

The employer (Aztec Lighting), wants to keep track of how many houses are getting completed by their work crews daily (not just the number of houses, but also the number of fixtures installed, and the times at which these houses are started and finished).  Their solution was to ask people to write this information down on paper and hand that paper in at the end of each day (which from my experience there did not work well, as people simply did not comply, or would lose the paper, or fill it out disingenuously). 

My solution:

Have an application replace the pen and paper.  There is a login/signup feature that allows each user to have a name/pin combination, that is associated with a phone number for their supervisor.  Once signed up, the user simply opens the application, logs in, and clicks start when a house is being started. The date and time is logged at this point, and a Room database allows this information (the date and time, and the fact that a house is in progress) to persist in case the application is closed/shut down.  When the house is complete, the finish button is clicked, the date and time is logged again, and the user inputs a number of "pieces" (fixtures) that were completed at the house.  This continues throughout the day, and at the end of the day clicking done will generate a text message, containing that information, to be sent to the number of the users supervisor. As this information is useful to the employee when asking for a raise (they have documentation of their work, including totals for houses, pieces, and a piece per hour rate for each one) it is then saved to the overall record of the user.  This record can be viewed, deleted, or emailed.  The number to send the daily sms message to can be changed from the main screen at any time, as the supervisor any employee is reporting to can change frequently.  

The application uses:
- Room
- Kotlin Coroutines
- MVVM architecture
