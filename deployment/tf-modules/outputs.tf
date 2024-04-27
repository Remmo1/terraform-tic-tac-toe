output "tic-tac-toe-ec2-address" {
    value = aws_instance.tic_tac_toe-tf.public_ip
}
