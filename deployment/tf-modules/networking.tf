resource "aws_vpc" "vpc_tf" {
  cidr_block           = "10.0.0.0/24"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "tic-tac-toe-tf"
  }
}

resource "aws_subnet" "subnet_tf" {
  vpc_id     = aws_vpc.vpc_tf.id
  cidr_block = "10.0.0.0/28"

  tags = {
    Name = "tic-tac-toe-tf"
  }
}

resource "aws_internet_gateway" "igw_tf" {
  vpc_id = aws_vpc.vpc_tf.id

  tags = {
    Name = "tic-tac-toe-tf"
  }
}

resource "aws_route_table" "rt_tf" {
  vpc_id = aws_vpc.vpc_tf.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw_tf.id
  }

  tags = {
    Name = "tic-tac-toe-tf"
  }

}

resource "aws_route_table_association" "subnet_tf" {
  subnet_id      = aws_subnet.subnet_tf.id
  route_table_id = aws_route_table.rt_tf.id
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
