make:
	javac -d bin/ client/*.java
	javac -d bin/ server/*.java
	javac -d bin/ utils/*.java

run_client:
	java -cp bin/ client/Client $(HOST) $(PORT)

run_server:
	java -cp bin/ server/Server $(PORT)

HOST ?= localhost
PORT ?= 8080

clean:
	rm -rf bin
