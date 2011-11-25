DEB_PACKAGE      = repochx
DEB_VERSION      := `cat debian/repochx/DEBIAN/control | grep -E "^Version:" | cut -f 2 -d ":" | tr -d " "`
DEB_RELEASE_FILE := $(DEB_PACKAGE)_$(DEB_VERSION)

all: jar

jar:
	ant jar

dpkg: jar
	[ -d debian/repochx/usr/share/java/ ] || mkdir -p debian/repochx/usr/share/java/
	cp java/target/REpochX.jar debian/repochx/usr/share/java/
	dpkg -b debian/repochx $(DEB_RELEASE_FILE).deb
