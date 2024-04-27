resource "aws_key_pair" "deployer" {
  key_name = var.key_name
  public_key = "file(../keys/${var.key_name}.pub)"
}
