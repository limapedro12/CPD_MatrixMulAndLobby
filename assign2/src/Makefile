make:
	javac -d bin/ client/*.java
	javac -d bin/ server/*.java
	javac -d bin/ server/lobby/*.java
	javac -d bin/ utils/*.java
	# Do not change

run_client:
	java -cp bin/ client/Client
	# Change to name of the "conducting" class

run_server:
	java -cp bin/ server/Server 8080
	# Change to name of the "conducting" class

clean:
	rm -rf bin
