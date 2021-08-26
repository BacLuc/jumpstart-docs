Basics
======


Das Dockerfile
--------------

* besteht aus Docker *Kommandos*: [dockerfile_reference]
* beschreibt das *Bauen* eines Images
* wird *sequentiell* abgearbeitet $\to$ jeder Schritt ein Layer
* `RUN` führt normale *Shell-Befehle* aus

\colBegin{0.45}

~~~ {.dockerfile}
FROM ubuntu:18.04

# install all needed packages
RUN apt-get update \
    && apt-get install -y \
        git-core rsync

# create user
ARG user=sdkuser
ARG group=sdkuser
RUN groupadd -o -g ${gid} ${group}
~~~

\colNext{0.55}

~~~ {.dockerfile}
ARG uid=1000
ARG gid=1000
RUN useradd --no-log-init -d "/home/${user}" \
    -u ${uid} -g ${gid} -m -s /bin/bash ${user}

# change to normal user
USER ${user}
WORKDIR /home/${user}

# prepare custom install steps
COPY my_install_script.sh /home/${user}/
RUN /home/${user}/my_install_script.sh
~~~

\colEnd


Build Context
-------------

* wird an Docker Deamon *gesendet* (kopiert)
* Dockerfile kann *nur auf Dateien im Kontext* zugreifen
* was kopiert wird, kann mittels *`.dockerignore`* eingeschränkt werden

~~~ {.bash}
# hier wird "Dockerfile" im Kontext "." gesucht
docker build .

# hier wird das Dockerfile explizit angegeben (kann ausserhalb des Kontext liegen)
docker build -f /path/to/a/docker_file .
~~~


Management
----------

\colBegin{0.5}

*Images erzeugen*

~~~ {.bash}
docker build .
docker build --tag <image_tag> .
~~~

*Images verwalten*

~~~ {.bash}
docker images
docker rmi <image_tag/hash>
docker image prune
~~~

*Interaktiv*

~~~ {.bash}
docker run -it <image_tag>
docker run -it --rm <image_tag>

docker exec -it <container_name/hash> \
    /bin/bash
docker logs -f -t <container_name/hash>
~~~

\colNext{0.5}

*Container erzeugen/starten*

~~~ {.bash}
docker create <image_tag>
docker create --name <container_name> \
    <image_tag>

# create and run
docker run --name <container_name> <image_tag>
docker run -d --rm --privileged <image_tag>
docker run -it --rm <image_tag> /bin/bash
~~~

*Container verwalten*

~~~ {.bash}
docker ps -a
docker rm <container_name/hash>

docker start <container_name/hash>
docker stop <container_name/hash>

docker inspect <container_name/hash>
~~~

\colEnd


Persistenz
----------

* Container soll *Wegwerf-Ware* sein
* Persistenz wird *ausserhalb* gelöst (nicht im Container-Layer)

\centering
![docker_mounts](images/docker_storage.pdf){width=65%}

### Varianten

* Volumes: [docker_volumes]
* Bind-Mounts: [docker_bind_mounts]
* tmpfs-Mounts: [docker_tmpfs_mounts]


Volumes
-------

* Volumes werden von `docker` *verwaltet*
* nicht existierende Volumes werden automatisch *erstellt*
* vorhandene Daten am Mount-Point werden *kopiert*
* Volumes können zwischen Container *geteilt* werden

*Management*

~~~ {.bash}
docker volume create <vol_name>
docker volume ls
docker volume rm <vol_name>
docker volume prune
~~~

*Verwendung*

~~~ {.bash}
# --mount (more explicit)
docker run --mount source=<vol_name>,target=/app alpine
# -v (shorter)
docker run -v <vol_name>:/app alpine
~~~


Bind-Mounts
-----------

* vorhandene Daten am Mount-Point werden *überdeckt*
* einfacher Weg um Daten *rein oder raus* zu bekommen
* langsam auf Windows & Mac

*Verwendung*

~~~ {.bash}
# --mount (more explicit)
docker run --mount type=bind,source=/my/local/app,target=/app,readonly alpine

# -v (shorter)
docker run -v /my/local/app:/app:ro alpine
~~~


tmpfs-Mounts
------------

* für nicht persistente, *temporäre* Daten
* nur auf Linux
* kann nicht zwischen Container geteilt werden

*Verwendung*

~~~ {.bash}
# --mount (more explicit)
docker run --mount type=tmpfs,target=/app alpine

# --tmpfs (no options)
docker run --tmpfs /app alpine
~~~


Netzwerk
--------

Verschiedene *Driver* verfügbar:

* `bridge`: Default-Netz. Isoliert Container von Host
* `host`: Keinerlei Isolation des Netzwerks
* `none`: Totale Isolation $\to$ kein Netzwerk
* `overlay`: Erlaubt die Verbindung mehrere Docker Daemons
* `macvlan`: Container hat MAC-Adresse und erscheint als "echtes" Gerät im Netzwerk



Bridge-Netzwerk
---------------

* Alle Container im Default-Netz sind *verbunden*
  * können aber nur mit IP adressiert werden
* Container in einem *user-defined* Netzwerk sind mit *Container-Namen* adressierbar

*Verwendung*

~~~ {.bash}
docker network create --driver bridge <network_name>
docker network ls
docker run --network <network_name> alpine
docker network inspect <network_name>
docker network connect <network_name> <container_name/hash>
~~~

[docker_network_bridge]


Netzwerk-Ports
--------------

*Dokumentation* relevanter Ports im `Dockerfile`

~~~ {.dockerfile}
EXPOSE 22
EXPOSE 23/tcp
EXPOSE 24/udp
~~~

Müssen trotzdem explizit beim Starten *geöffnet* werden.

* im gleichen Netzwerk sind alle Ports direkt verfügbar
* aber z.B: bei `bridge` nicht für den Host geöffnet

~~~ {.bash}
docker run -p 2222:22 -p 2323:23/tcp ...
~~~

Es können auch Ports geöffnet werden die *nicht* `EXPOSE` sind.


Netzwerk-IP-Pools
-----------------

Docker *Default-Netzwerk*: `172.18.0.1`
$\break\to$ überschneidet sich genau mit dem *SCS internen Netz*!
  
Sollte *immer* im Docker Daemon konfiguriert werden: `/etc/docker/daemon.json`

~~~ {.json}
{
  "bip": "172.28.0.1/24",
  "default-address-pools": [
    {"base":"172.28.0.0/16", "size":24}
  ],
  "dns": [
    "172.18.0.100", "172.18.0.101", "172.20.4.1", "172.24.0.101", "8.8.8.8", "8.8.4.4"
  ]
}
~~~


Image-Registry
--------------

* Image bei jedem Deployment bauen ist unnötiger *Overhead*
* Images einmal bauen und *zentral* zur Verfügung stellen
* Versionierung mittels *Tags*
* Images für unterschiedliche *Architekturen*: arm, x86, etc.

*Registries*

* Docker Hub
* Artifactory
* GitHub Packages
* private

~~~ {.bash}
# upload image to registry
docker push <your_username>/<image_repo>

# is automatically done by using in Dockerfile or create/run command
docker pull <your_username>/<image_repo>
~~~