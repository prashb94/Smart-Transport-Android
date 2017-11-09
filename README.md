# Smart-Energy-Project-1

A personal carbon footprint monitor android application completed as part of the requirement for the Smart Energy course at ETH Zurich, Fall 2017, created in the month of October.

The application tracks the user's location and infers the mode of transport using the Google Activity Detection API.

Further, the vehicle classification mode is broken down into Tram/Car using the following methodology -

While travelling in a tram, the activity observed was blocks of - "Standing Still" and blocks of "In Vehicle" with a certain maximum speed.
The logic used here is that such a pattern indicates with a high probability that the user is in a tram. Integrating google maps is an option but that too comes with the limit of experimental error.
Eg: The car could be travelling along a tram route and during traffic, leading to an inference of tram. This approach too comes with a certain error, but can be finetuned with the provided parameters to reduce the error as much as possible.
First, to observe a tram pattern as described above, we aggregate a set number of past records (@param) and figure out the blocks in this pattern and convert them to percentages, while also
calculating the average speed.
Now we define additionally a tolerance (@param) that can be used to finetune the differentiator. If there are equal blocks of "Standing Still" and "Vehicle" or nearly equal (Plus or minus tolerance)
and if the current speed is close to the average speed in this observation interval we can say with a high probability that the user is in a Tram/Bus. This probability depends on the country/location
and the combination of parameters.

There are many possible improvements -

- Move all database activities off the UI thread onto a separate thread, in MainActivity
- ML based tram/car classifier
- Additional charts
- Push notifications
- Notifications about CO2 consumption
