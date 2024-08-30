SELECT
    -- Identifier, which comes from "generate_series"
    s as id,
    -- Pick a random first name from 'firstnames' array
    arrays.firstnames[trunc(random() * ARRAY_LENGTH(arrays.firstnames, 1) + 1)] AS firstname,
    -- Pick a random middle name character
    substring('ABCDEFGHIJKLMNOPQRSTUVWXYZ' from trunc(random() * 26 + 1)::int for 1) AS middlename,
    -- Pick a random last name from 'lastnames' array
    arrays.lastnames[trunc(random() * ARRAY_LENGTH(arrays.lastnames,1) + 1)] AS lastname,
    -- Pick number birthdate
  date(now() - trunc(random() * 365 * 82 /*max age + 18*/) * '1 day'::interval - interval '18 year' /* min age*/) as birth_date,
    -- Generate random SSN number
  100000000 + round(random() * 900000000) as ssn,
    -- Generate random amount
  round((random() * 100000)::numeric, 2) as amount,
    -- Generate random house number
  (100 + random() * 9900)::int as house,
    -- Generate random stree name from 'streets'
  arrays.streets[trunc(random() * ARRAY_LENGTH(arrays.streets, 1) + 1)]  AS street,
    -- Choose random street type
  case (random() * 2)::int when 0 then 'St.' when 1 then 'Ave.' when 2 then 'Rd.' end as street_type,
    -- Pick random city
  arrays.cities[trunc(random() * ARRAY_LENGTH(arrays.cities, 1) + 1)] AS city,
    -- Pick random state
  arrays.states[trunc(random() * ARRAY_LENGTH(arrays.states, 1) + 1)] AS state,
    -- Generate random phone number
  concat(FLOOR(100 + random() * 900), ' ', FLOOR(100 + random() * 900), ' ', FLOOR(1000 + random() * 9000)) as phone
FROM
    -- Number of rows to generate
    generate_series(1, 1000) AS s
CROSS JOIN(
    SELECT ARRAY[
    'Adam', 'Bill', 'Bob', 'Donald', 'Frank', 'George', 'James', 'John', 'Jacob', 'Jack', 'Martin', 'Matthew', 'Max', 'Michael', 'Paul','Peter', 'Ronald',
    'Samuel','Steve','William', 'Abigail', 'Alice', 'Amanda', 'Barbara','Betty', 'Carol', 'Donna', 'Jane','Jennifer','Julie','Mary','Melissa','Sarah','Susan'
    ] AS firstnames,
    ARRAY[
        'Matthews','Smith','Jones','Davis','Jacobson','Williams','Donaldson','Maxwell','Peterson','Stevens', 'Franklin','Washington','Jefferson','Adams',
        'Jackson','Johnson','Lincoln','Grant','Fillmore','Harding','Taft', 'Truman','Nixon','Ford','Carter','Reagan','Bush','Clinton','Hancock'
    ] AS lastnames,
    ARRAY[
    'Green', 'Smith', 'Church', 'Grant', 'Cedar', 'Forest', 'Frankl', 'Birch', 'Jones', 'Brown',
    'Cherry', 'Willow', 'Rose', 'School', 'Wilson', 'Center', 'Walnut', 'Mill', 'Valley'
    ] as streets,
    ARRAY[
    'Washington', 'Franklin', 'Clinton', 'Georgetown', 'Springfield', 'Chester', 'Greenville', 'Dayton', 'Madison', 'Salem',
    'Winchester', 'Oakland', 'Milton', 'Newport', 'Ashland', 'Riverside', 'Manchester', 'Oxford', 'Burlington', 'Jackson', 'Milford'
    ] as cities,
    ARRAY[
    'AK','AL','AR','AZ','CA','CO','CT','DC','DE','FL','GA','HI','IA','ID','IL','IN','KS','KY','LA','MA','MD','ME','MI','MN','MS','MO',
    'MT','NC','NE','NH','NJ','NM','NV','NY','ND','OH','OK','OR','PA','RI','SC','SD','TN','TX','UT','VT','VA','WA','WV','WI','WY'
    ] as states
) AS arrays