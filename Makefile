all: jar

jar:
	ant jar

dpkg: jar
	[ -d debian/repochx/usr/share/java/ ] || mkdir -p debian/repochx/usr/share/java/
	cp java/target/REpochX.jar debian/repochx/usr/share/java/
	dpkg -b debian/repochx repochx.deb


