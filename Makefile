.PHONY: compile run runserver default kill

CLASSPATH=.:/usr/local/apache-river-3.0.0/lib/jsk-lib.jar:/usr/local/apache-river-3.0.0/lib/jsk-platform.jar:/usr/local/apache-river-3.0.0/lib/outrigger.jar:/usr/local/apache-river-3.0.0/lib/reggie.jar:/usr/local/apache-river-3.0.0/lib-dl/outrigger-dl.jar:

compile:
	cd src && javac -classpath $(CLASSPATH) *.java

run:
	cd src && java -classpath $(CLASSPATH) -Djava.security.policy=../policy.all -Djava.rmi.server.useCodebaseOnly=false BulletinBoardClient

runserver:
	cd /usr/local/apache-river-3.0.0/start-scripts && ./start-all-services.sh

kill:
	killall java

default: compile run

.DEFAULT_GOAL := default
