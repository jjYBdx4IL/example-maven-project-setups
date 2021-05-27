## Run test

    mvn clean install

## Start webapp and keep it running

    mvn clean install -DskipITs wildfly:run

`install` is needed to configure the instance.

At least on Windows, `mvn wildfly:start` only works if you keep the CMD process open.

The fastest way to auto-redeploy the webapp on recompiled classes (ie. when editing
using Eclipse) is https://github.com/jjYBdx4IL/snippets/blob/master/java/jee_autodeploy.sh

## Access the database

Either via `java -jar ~/.m2/repository/**/h2-*.jar` in offline mode or via

    git clone https://github.com/jjYBdx4IL/misc.git
    cd misc/h2-frontend
    mvn clean wildfly:deploy -Dwildfly.port=9992
    xdg-open http://localhost:8082/h2/h2

while the Wildfly instance is online.

The database URL is "jdbc:h2:./target/db", user sa, pwd none.

## HDBC databases and time zones

Here are my observations:

### h2-frontend via java -jar j2-*.jar

The output shown for `SELECT * FROM Article` via that frontend does not change based
on the user's time zone settings. It always shows the time zone setting that has been
in effect for the jdbc layer when the timestamp has been stored (btw. there is a
Hibernate jdbc.time_zone setting that's by default set to the user's time zone), ie.

    2021-05-14 21:38:18.409548+00

for TZ=UTC or

    <javaOpt>-Duser.timezone=UTC</javaOpt>

in pom.xml, and 

    2021-05-14 23:38:18.409548+02

when GMT is active (+02) during the `mvn clean install` run producing the timestamp
in `./target/db`. However, that seems to be a purely cosmetic effect, because the
actual time zone compensated time remains the same. Just make sure you are using JPA 2.2
and a recent h2 database version (ie. you are actually using the "TIMESTAMP WITH TIME ZONE"
SQL type, which you can check using the h2 frontend and by unfolding the table on the
left hand side).

### Conclusion

Make sure you are using the correct SQL time format. Avoid java.util.Date. Use
java.time.(Instant|OffsetDateTime) instead. Those types are also immutable and thread-safe.
Also, just to be on the safe side and for situation where you don't have control of the 
production server setup, you can set `hibernate.jdbc.time_zone` to `UTC` in `persistence.xml`.
