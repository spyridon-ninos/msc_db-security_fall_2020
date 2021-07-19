# NOTE: 
This is a project done for the DB Security course, Fall 2020, Information and Communication Systems Security M.Sc. program, Univ. of the Aegean, Greece.
  
The code is *decent*; the purpose of this project was to make an analysis of the given data set, rather than the code itself. 

# THANKS 
...to the other members of the team (Christos and Margarita), who gave their permission to publish the code (make the repo public).
  
***
  
# How to build the code

## Dependencies
- jdk (java) v14 

## Install the libarx library
#### Note: although the dependency is needed to compile the project, the library is not used anywhere
- go in the libarx directory
- run: `./install_arx_lib.sh` (this will install the **libarx v3.8.0** in the local maven repo, in the `~/.m2` directory)

## Run mvn
- go to the top project directory
- run:  `mvn clean verify`

## Run the program
- go into the `target` directory
- run: `./dbprivacy-1.0.0.jar` or `java -jar dbprivacy-1.0.0.jar` (whatever you prefer)
