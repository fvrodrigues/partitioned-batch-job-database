# partitioned-batch-job

 Nesta poc de particionamento, utilizei o banco de dados MYSQL no kubernetes [Informacoes sobre url etc .. no arquivo application.properties] :
   
   * DATABASE : batch
   
        CREATE DATABASE batch; 
        ALTER DATABASE batch CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; 
        
         CREATE DATABASE batch 
         ALTER DATABASE batch CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;                  
                            
   * TABELAS / REGISTROS 
        
         CREATE TABLE customer (
            id INT PRIMARY KEY AUTO_INCREMENT ,
            firstName VARCHAR(255) NULL,
            lastName VARCHAR(255) NULL,
            birthdate VARCHAR(255) NULL);
         
          CREATE TABLE new_customer (
            id INT PRIMARY KEY,
            firstName VARCHAR(255) NULL,
            lastName VARCHAR(255) NULL,
            birthdate VARCHAR(255) NULL);
            
          CREATE TABLE customer (
             id INT PRIMARY KEY AUTO_INCREMENT ,
             message VARCHAR(500) NULL);
              
             
       

         
         
