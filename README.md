## CS 425 - MP1

# To Build

Run `mvn package` from the root of the directory. The built jar will be  `target/*-jar-with-dependencies.jar`.

# Step 1: Socket Programming 

In step 1, create a socket connection between each pair of processes in your system. A configuration file​ should contain the identifiers, IP addresses and ports to  use for the  processes. Each  process  will  have  a unique integer identifier (such as 1, 2, 3…). Implement basic unicast functionality:
* __unicast_send(destination,message)__: Sends message to the destination process.
* __unicast_receive(source,message)__​: Delivers the message received from the 
source process

