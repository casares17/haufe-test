-- Insert categories
INSERT INTO manufacturers (name, country)
VALUES ('Manufacturer 1', 'Spain'),
       ('Manufacturer 2', 'Portugal');

-- Insert products
INSERT INTO beers (name, alcohol_by_volume, beer_type, manufacturer_id)
VALUES ('Beer 1', 3.50, 'IPA', 1),
       ('Beer 2', 4.00, 'STOUT', 1),
       ('Beer 3', 4.50, 'LAGGER', 2);
