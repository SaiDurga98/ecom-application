// Migrating to PostgreSQL using docker
docker run -d --name db -e POSTGRES_PASSWORD=saidurga postgres:14
docker run -d --name pgadmin -e PGADMIN_DEFAULT_EMAIL=user@domain.com -e PGADMIN_DEFAULT_PASSWORD=saidurga dpage/pgadmin4
docker exec -it pgadmin ping db -> ping: bad address 'db'
=> Now db should be accessible to pgadmin and that can be acheived using docker networks helps talking to other containers
docker network create my-network
docker rm -f db pgadmin : Removing containers created previously
docker run -d --name db --network my-network -e POSTGRES_PASSWORD=saidurga postgres:14 
docker run -d --name pgadmin --network my-network -e PGADMIN_DEFAULT_EMAIL=user@domain.com -e PGADMIN_DEFAULT_PASSWORD=saidurga dpage/pgadmin4
=> Now
docker exec -it pgadmin ping db
PING db (172.19.0.2): 56 data bytes
64 bytes from 172.19.0.2: seq=0 ttl=42 time=0.773 ms..

=> Now for our application
docker run -d --name postgres_container -e POSTGRES_USER=saidurga -e POSTGRES_PASSWORD=saidurga -e PGDATA=/data/postgres -v postgres:/data/postgres -p 5432:5432 --network postgres --restart unless-stopped postgres:14
docker run -d --name pgadmin_container -e PGADMIN_DEFAULT_EMAIL=pgadmin4@pgadmin.org -e PGADMIN_DEFAULT_PASSWORD=admin -e PGADMIN_CONFIG_SERVER_MODE=False -v pgadmin:/var/lib/pgadmin -p 5050:80 --network postgres --restart unless-stopped dpage/pgadmin4

=> Rather than doing above steps individually, we can write a single file Docker Compose yml file and create those containers
docker compose up -d
