provider "aws" {
  region  = "us-east-1"
  profile = "default"
}

resource "aws_instance" "ec2_tic_tac_toe" {
  ami                    = "ami-0c101f26f147fa7fd"
  instance_type          = "t2.medium"
  key_name               = "key-for-demo"
  associate_public_ip_address = true
  vpc_security_group_ids = [aws_security_group.main.id]

  tags = {
    Name = "Ec2 Tic-tac-toe tf"
  }

  # user_data = "${file("install-app.sh")}"
}

resource "aws_vpc" "vpc_tf" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "Vpc Tic-tac-toe tf"
  }
}

resource "aws_subnet" "subnet_tf" {
  vpc_id     = aws_vpc.vpc_tf.id
  cidr_block = "10.0.1.0/24"

  tags = {
    Name = "Subnet Tic-tac-toe tf"
  }
}

resource "aws_internet_gateway" "igw_tf" {
  vpc_id = aws_vpc.vpc_tf.id

  tags = {
    Name = "Gateway Tic-tac-toe tf"
  }
}

resource "aws_route_table" "rt_tf" {
  vpc_id = aws_vpc.vpc_tf.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw_tf.id
  }

  tags = {
    Name = "Route table Tic-tac-toe tf"
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
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    description = "nginx"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
}

resource "aws_key_pair" "deployer" {
  key_name = "key-for-demo"
  public_key = "${file("key-for-demo.pub")}"
}

# resource "null_resource" "run_ansible" {
#   depends_on = [aws_instance.ec2_tic_tac_toe]

#   provisioner "local-exec" {
#     command = "ansible-playbook -i inventory.ini deploy-app.yml"
#     working_dir = path.module
#   }
# }
