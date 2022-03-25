java/build:
		cd log4shell-vulnerable-app; gradle bootJar --no-daemon

build: java/build
		cp log4shell-vulnerable-app/build/libs/*.jar buildingmgmt.1.0-1/opt/buildingmgmt/building-management.jar
		dpkg-deb --build buildingmgmt.1.0-1

.PHONY: build
