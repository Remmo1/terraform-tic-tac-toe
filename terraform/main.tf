provider "aws" {
  region  = "us-east-1"
  profile = "default"
}

resource "aws_instance" "public_instance" {
  ami                    = "ami-0c101f26f147fa7fd"
  instance_type          = "t2.medium"
  key_name               = "key-for-demo"
  associate_public_ip_address = true
  vpc_security_group_ids = [aws_security_group.main.id]

  tags = {
    Name = "Tic tac toe"
  }
}

resource "aws_vpc" "vpc_a" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
}

resource "aws_subnet" "subnet_a" {
  vpc_id     = aws_vpc.vpc_a.id
  cidr_block = "10.0.1.0/24"
}

resource "aws_internet_gateway" "igw_a" {
  vpc_id = aws_vpc.vpc_a.id
}

resource "aws_route_table" "rt_a" {
  vpc_id = aws_vpc.vpc_a.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw_a.id
  }

}

resource "aws_route_table_association" "rta_subnet_a" {
  subnet_id      = aws_subnet.subnet_a.id
  route_table_id = aws_route_table.rt_a.id
}


resource "aws_security_group" "main" {
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    description = "SSH"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    description = "Backend"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  ingress {
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    description = "Frontend"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_key_pair" "deployer" {
  key_name = "key-for-demo"
  public_key = "key-for-demo.pub"
}

resource "null_resource" "run_ansible" {
  depends_on = [aws_instance.public_instance]

  provisioner "local-exec" {
    command = "ansible-playbook -i inventory.ini deploy-app.yml"
    working_dir = path.module
  }
}
