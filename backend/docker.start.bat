docker run -d --name neo4j --publish=7474:7474 --publish=7687:7687 --env NEO4J_AUTH=neo4j/neo4jpw --mount source=neo4j,target=/data neo4j