export HEROKU_API_KEY=b0b6147e-848e-4bdb-a9da-67fff6eb8af1

terraform init

terraform apply \
  -var "db_username=neondb_owner" \
  -var "db_password=npg_gnOfELK4JSo1" \
  -var "db_url=jdbc:postgresql://ep-withered-queen-ac4vnbsq-pooler.sa-east-1.aws.neon.tech/grainpaydb?sslmode=require"
