javac -d bin -sourcepath src -cp ./lib/ojdbc14.jar src/Students.java
java -cp bin:lib/ojdbc14.jar Students
