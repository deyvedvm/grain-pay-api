variable "app_name" {
  description = "ECS app name"
  type        = string
  default     = "springboot-app"
}

variable "db_username" {
  description = "Neon DB username"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Neon DB password"
  type        = string
  sensitive   = true
}

variable "db_url" {
  description = "Neon DB JDBC URL"
  type        = string
  sensitive   = true
}
