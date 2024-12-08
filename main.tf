terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region     = var.aws_region
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
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

resource "aws_security_group" "MySG" {
  vpc_id = aws_vpc.MyVpc.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # SSH Access
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # HTTP Access
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "MySecurityGroup"
  }
}


resource "aws_instance" "MyEC2" {
  ami = "ami-0e2c8caa4b6378d8c"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.MySubnet.id
  vpc_security_group_ids = [aws_security_group.MySG.id]
  tags = {
   name = "Instance-1" 
  }
}

