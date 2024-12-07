terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

resource "aws_vpc" "MyVpc" {
  cidr_block = "10.0.0.0/16"
  tags = {
    name = "Insance-VPC"
  }
  
}

resource "aws_subnet" "MySubnet" {
  vpc_id = aws_vpc.MyVpc.id
  cidr_block = "10.0.0.0/24"
  tags = {
    name = "Subnet-1"
  }
  
}

resource "aws_instance" "MyEC2" {
  ami = "ami-0e2c8caa4b6378d8c"
  instance_type = "t2.micro"
  tags = {
   name = "Instance-1" 
  }
}

