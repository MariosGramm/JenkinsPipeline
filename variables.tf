variable "aws_region" {
  type = string  
  description = "The AWS region to deploy resources"
}

variable "aws_access_key" {
  description = "AWS Access Key"
  type = string
  sensitive   = true
}

variable "aws_secret_key" {
  description = "AWS Secret Key"
  type = string
  sensitive   = true
}
