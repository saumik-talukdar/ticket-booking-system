#!/bin/bash

set -e

echo "Creating service databases and users..."


# =================================
# Identity Service
# =================================

psql -v ON_ERROR_STOP=1 \
    --username "$POSTGRES_USER" \
    --dbname postgres <<-EOSQL

    CREATE USER ${IDENTITY_DB_USER}
    WITH PASSWORD '${IDENTITY_DB_PASSWORD}';

    CREATE DATABASE ${IDENTITY_DB}
    OWNER ${IDENTITY_DB_USER};

EOSQL


# =================================
# Event Service
# =================================

psql -v ON_ERROR_STOP=1 \
    --username "$POSTGRES_USER" \
    --dbname postgres <<-EOSQL

    CREATE USER ${EVENT_DB_USER}
    WITH PASSWORD '${EVENT_DB_PASSWORD}';

    CREATE DATABASE ${EVENT_DB}
    OWNER ${EVENT_DB_USER};

EOSQL


# =================================
# Booking Service
# =================================

psql -v ON_ERROR_STOP=1 \
    --username "$POSTGRES_USER" \
    --dbname postgres <<-EOSQL

    CREATE USER ${BOOKING_DB_USER}
    WITH PASSWORD '${BOOKING_DB_PASSWORD}';

    CREATE DATABASE ${BOOKING_DB}
    OWNER ${BOOKING_DB_USER};

EOSQL


# =================================
# Payment Service
# =================================

psql -v ON_ERROR_STOP=1 \
    --username "$POSTGRES_USER" \
    --dbname postgres <<-EOSQL

    CREATE USER ${PAYMENT_DB_USER}
    WITH PASSWORD '${PAYMENT_DB_PASSWORD}';

    CREATE DATABASE ${PAYMENT_DB}
    OWNER ${PAYMENT_DB_USER};

EOSQL


echo "Service databases and users created successfully."