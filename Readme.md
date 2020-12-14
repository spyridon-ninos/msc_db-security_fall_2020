# How to build the code

## Dependencies
- jdk (java) v14 

## Install the libarx library
- go in the libarx directory
- run: `./install_arx_lib.sh` (this will install the **libarx v3.8.0** in the local maven repo, in the `~/.m2` directory)

## Run mvn
- go to the top project directory
- run:  `mvn clean verify`

## Run the program
- go in the `target` directory
- run: `./dbprivacy-1.0.0.jar` or `java -jar dbprivacy-1.0.0.jar` (whatever you prefer)