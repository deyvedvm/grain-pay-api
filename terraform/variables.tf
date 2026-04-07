variable "app_name" {
  description = "Application name used to prefix all AWS resources"
  type        = string
  default     = "grain-pay-api"
}

variable "db_username" {
  description = "PostgreSQL username"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "PostgreSQL password"
  type        = string
  sensitive   = true
}

variable "db_url" {
  description = "PostgreSQL JDBC URL (e.g. jdbc:postgresql://host/db?sslmode=require)"
  type        = string
  sensitive   = true
}
