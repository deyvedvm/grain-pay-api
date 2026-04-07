#!/usr/bin/env bash
set -euo pipefail

# Credenciais do banco devem ser passadas via variáveis de ambiente:
#
#   export TF_VAR_db_username="seu_usuario"
#   export TF_VAR_db_password="sua_senha"
#   export TF_VAR_db_url="jdbc:postgresql://host/db?sslmode=require"
#
# Alternativamente, crie um arquivo terraform.tfvars (já no .gitignore):
#
#   db_username = "seu_usuario"
#   db_password = "sua_senha"
#   db_url      = "jdbc:postgresql://host/db?sslmode=require"

terraform init
terraform apply
