mvn package

mkdir -p /opt/silo
cp ./target/silo-0.0.2-SNAPSHOT.jar /opt/silo/silo.jar
cp silo.yaml /opt/silo/silo.yaml
cp ./script/initd /etc/init.d/silo
chmod 755 /etc/init.d/silo
update-rc.d silo defaults
