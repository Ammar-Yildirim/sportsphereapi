
CREATE TABLE users (
    id uuid NOT NULL DEFAULT RANDOM_UUID(),
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    enabled BOOLEAN NOT NULL,
    role varchar(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);


CREATE TABLE locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude NUMERIC(10,7) NOT NULL,
    longitude NUMERIC(10,7) NOT NULL,
    city VARCHAR(255),
    country VARCHAR(255),
    formatted_address VARCHAR(255) not null,
    CONSTRAINT locations_unique_lat_lng UNIQUE (latitude, longitude)
);


CREATE TABLE events (
    id uuid not null default RANDOM_UUID(),
    created_by uuid not NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    starts_at TIMESTAMP NOT NULL,
    sport_category VARCHAR(100) NOT NULL,
    sport_name VARCHAR(100) NOT NULL,
    team_number INTEGER NOT NULL,
    player_number INTEGER NOT NULL,
    location_id INTEGER not null,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint "sports_events_pkey" PRIMARY KEY (id),
    CONSTRAINT events_fk_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT events_fk_location FOREIGN KEY (location_id) REFERENCES locations(id)
);

create table event_participation(
    event_id uuid not null,
    user_id uuid not null,
    team INTEGER not null check (team in(1,2)),
    spot INTEGER not null check (spot >= 1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint event_participation_fk_event_id foreign key (event_id) references events(id),
    constraint event_participation_fk_user_id foreign key (user_id) references users(id),
    constraint event_participation_unique_eventID_team_spot unique (event_id, team, spot),
    primary key (event_id, user_id)
);

CREATE TABLE comments (
    id INT AUTO_INCREMENT PRIMARY KEY, -- Changed from SERIAL
    event_id uuid NOT NULL,
    user_id uuid NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT comment_fk_event_id FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT comment_fk_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE ALIAS IF NOT EXISTS EARTH_DISTANCE FOR "com.sportsphere.sportsphereapi.util.H2UtilFunctions.earthDistance";
CREATE ALIAS IF NOT EXISTS LL_TO_EARTH FOR "com.sportsphere.sportsphereapi.util.H2UtilFunctions.llToEarth";
