#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE user_service_db;
    CREATE DATABASE dog_service_db;
    CREATE DATABASE rating_service_db;

    CREATE USER user_svc_user WITH PASSWORD '$USER_SVC_DB_PASSWORD';
    CREATE USER dog_svc_user WITH PASSWORD '$DOG_SVC_DB_PASSWORD';
    CREATE USER rating_svc_user WITH PASSWORD '$RATING_SVC_DB_PASSWORD';

    REVOKE ALL ON DATABASE user_service_db FROM PUBLIC;
    REVOKE ALL ON DATABASE dog_service_db FROM PUBLIC;
    REVOKE ALL ON DATABASE rating_service_db FROM PUBLIC;

    GRANT CONNECT ON DATABASE user_service_db TO user_svc_user;
    GRANT CONNECT ON DATABASE dog_service_db TO dog_svc_user;
    GRANT CONNECT ON DATABASE rating_service_db TO rating_svc_user;
EOSQL