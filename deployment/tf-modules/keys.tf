resource "aws_key_pair" "deployer" {
  key_name = var.key_private
  public_key = file(var.key_public)
}

# ssh-keygen -b 2048 -t rsa
