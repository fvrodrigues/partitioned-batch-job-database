# partitioned-batch-job

 Neste poc de particionamento, utilizei o banco de dados mysql :
   
   * SCHEME : batch
   * TABELAS / REGISTROS 
        
         CREATE TABLE customer (
            id INT PRIMARY KEY,
            firstName VARCHAR(255) NULL,
            lastName VARCHAR(255) NULL,
            birthdate VARCHAR(255) NULL);
         
          CREATE TABLE new_customer (
            id INT PRIMARY KEY,
            firstName VARCHAR(255) NULL,
            lastName VARCHAR(255) NULL,
            birthdate VARCHAR(255) NULL);
    
         INSERT INTO customer VALUES ('1', 'John', 'Doe', '10-10-1952 10:10:10');
         INSERT INTO customer VALUES ('2', 'Amy', 'Eugene', '05-07-1985 17:10:00');
         INSERT INTO customer VALUES ('3', 'Laverne', 'Mann', '11-12-1988 10:10:10');
         INSERT INTO customer VALUES ('4', 'Janice', 'Preston', '19-02-1960 10:10:10');
         INSERT INTO customer VALUES ('5', 'Pauline', 'Rios', '29-08-1977 10:10:10');
         INSERT INTO customer VALUES ('6', 'Perry', 'Burnside', '10-03-1981 10:10:10');
         INSERT INTO customer VALUES ('7', 'Todd', 'Kinsey', '14-12-1998 10:10:10');
         INSERT INTO customer VALUES ('8', 'Jacqueline', 'Hyde', '20-03-1983 10:10:10');
         INSERT INTO customer VALUES ('9', 'Rico', 'Hale', '10-10-2000 10:10:10');
         INSERT INTO customer VALUES ('10', 'Samuel', 'Lamm', '11-11-1999 10:10:10');
         INSERT INTO customer VALUES ('11', 'Robert', 'Coster', '10-10-1972 10:10:10');
         INSERT INTO customer VALUES ('12', 'Tamara', 'Soler', '02-01-1978 10:10:10');
         INSERT INTO customer VALUES ('13', 'Justin', 'Kramer', '19-11-1951 10:10:10');
         INSERT INTO customer VALUES ('14', 'Andrea', 'Law', '14-10-1959 10:10:10');
         INSERT INTO customer VALUES ('15', 'Laura', 'Porter', '12-12-2010 10:10:10');
         INSERT INTO customer VALUES ('16', 'Michael', 'Cantu', '11-04-1999 10:10:10');
         INSERT INTO customer VALUES ('17', 'Andrew', 'Thomas', '04-05-1967 10:10:10');
         INSERT INTO customer VALUES ('18', 'Jose', 'Hannah', '16-09-1950 10:10:10');
         INSERT INTO customer VALUES ('19', 'Valerie', 'Hilbert', '13-06-1966 10:10:10');
         INSERT INTO customer VALUES ('20', 'Patrick', 'Durham', '12-10-1978 10:10:10');