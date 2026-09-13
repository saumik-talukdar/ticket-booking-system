CREATE TABLE users (
                       id UUID PRIMARY KEY,

                       email VARCHAR(255) NOT NULL UNIQUE,

                       password_hash VARCHAR(255) NOT NULL,

                       first_name VARCHAR(100) NOT NULL,

                       last_name VARCHAR(100) NOT NULL,

                       role VARCHAR(50) NOT NULL,

                       status VARCHAR(50) NOT NULL,

                       created_at TIMESTAMP WITH TIME ZONE NOT NULL,

                       updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);