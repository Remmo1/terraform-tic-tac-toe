resource "aws_instance" "tic_tac_toe-tf" {
  ami                    = var.ami
  instance_type          = var.instance_type
  key_name               = var.key_name
  associate_public_ip_address = true
  vpc_security_group_ids = [aws_security_group.main.id]

  tags = {
    Name = "tic-tac-toe-tf"
  }
}
