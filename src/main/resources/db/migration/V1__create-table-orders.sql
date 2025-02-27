CREATE TABLE orders(
    id char(36),
    asset_code varchar(10) not null,
    type varchar(10) not null,
    quantity double not null,
    price double not null,
    owner varchar(255),

    primary key (id)
)
